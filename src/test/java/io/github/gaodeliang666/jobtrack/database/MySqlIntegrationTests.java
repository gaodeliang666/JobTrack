package io.github.gaodeliang666.jobtrack.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import io.github.gaodeliang666.jobtrack.user.context.CurrentUserProvider;
import io.github.gaodeliang666.jobtrack.user.service.CurrentUserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("mysql-test")
@ContextConfiguration(initializers = MySqlTestSafetyGuard.class)
@EnabledIfSystemProperty(named = "jobtrack.mysql.tests", matches = "true")
@Transactional
class MySqlIntegrationTests {

    private static final Set<String> EXPECTED_BUSINESS_TABLES = Set.of(
            "app_user", "company", "job", "job_application", "interview", "communication");
    private static final Set<String> EXPECTED_SCHEMA_TABLES = Set.of(
            "app_user", "company", "job", "job_application", "interview", "communication",
            "flyway_schema_history");
    private static final Map<String, List<String>> EXPECTED_COLUMNS = Map.of(
            "app_user", List.of("id", "created_at", "updated_at"),
            "company", List.of("id", "user_id", "created_at", "updated_at"),
            "job", List.of("id", "user_id", "company_id", "created_at", "updated_at"),
            "job_application", List.of("id", "user_id", "job_id", "created_at", "updated_at"),
            "interview", List.of("id", "application_id", "created_at", "updated_at"),
            "communication", List.of("id", "application_id", "created_at", "updated_at"));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserIsolationTestMapper userIsolationTestMapper;

    @Test
    void shouldExposeOnlyExpectedSchemaTablesAndVersionOneHistory() {
        List<String> tables = loadSchemaTables();

        assertEquals(EXPECTED_SCHEMA_TABLES, new HashSet<>(tables));
        assertEquals(EXPECTED_SCHEMA_TABLES.size(), tables.size());
        assertEquals(1, successfulVersionOneMigrationCount());
    }

    @Test
    void shouldExposeExpectedColumnMetadata() {
        List<ColumnMetadata> columns = jdbcTemplate.query("""
                SELECT table_name, column_name, data_type, column_type, is_nullable,
                       column_default, extra, datetime_precision, ordinal_position
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name IN (
                      'app_user', 'company', 'job', 'job_application',
                      'interview', 'communication'
                  )
                ORDER BY table_name, ordinal_position
                """, MySqlIntegrationTests::mapColumnMetadata);

        Map<String, List<ColumnMetadata>> columnsByTable = columns.stream()
                .collect(Collectors.groupingBy(
                        ColumnMetadata::tableName,
                        LinkedHashMap::new,
                        Collectors.toList()));

        assertEquals(EXPECTED_COLUMNS.keySet(), columnsByTable.keySet());
        EXPECTED_COLUMNS.forEach((tableName, expectedColumnNames) -> assertEquals(
                expectedColumnNames,
                columnsByTable.get(tableName).stream().map(ColumnMetadata::columnName).toList(),
                () -> "Unexpected columns for table " + tableName));
        columns.forEach(this::assertColumnMetadata);
    }

    @Test
    void shouldExposeExpectedIndexesInDefinedColumnOrder() {
        List<IndexColumn> indexes = jdbcTemplate.query("""
                SELECT table_name, index_name, column_name, seq_in_index, non_unique
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name IN (
                      'app_user', 'company', 'job', 'job_application',
                      'interview', 'communication'
                  )
                ORDER BY table_name, index_name, seq_in_index
                """, (resultSet, rowNumber) -> new IndexColumn(
                        resultSet.getString("table_name"),
                        resultSet.getString("index_name"),
                        resultSet.getString("column_name"),
                        resultSet.getInt("seq_in_index"),
                        resultSet.getInt("non_unique") == 0));

        Set<IndexColumn> actualIndexes = new HashSet<>(indexes);
        assertEquals(expectedIndexes(), actualIndexes);
        assertEquals(expectedIndexes().size(), indexes.size());

        // MySQL exposes every PRIMARY KEY index as PRIMARY, even when the migration
        // supplies a logical constraint symbol such as pk_app_user.
        assertPrimaryIndex(actualIndexes, "app_user", "pk_app_user");
        assertPrimaryIndex(actualIndexes, "company", "pk_company");
        assertPrimaryIndex(actualIndexes, "job", "pk_job");
        assertPrimaryIndex(actualIndexes, "job_application", "pk_job_application");
        assertPrimaryIndex(actualIndexes, "interview", "pk_interview");
        assertPrimaryIndex(actualIndexes, "communication", "pk_communication");
    }

    @Test
    void shouldExposeExpectedForeignKeysAndRestrictRules() {
        List<ForeignKeyColumn> foreignKeys = jdbcTemplate.query("""
                SELECT k.table_name, k.constraint_name, k.column_name,
                       k.referenced_table_name, k.referenced_column_name,
                       k.ordinal_position, r.update_rule, r.delete_rule
                FROM information_schema.key_column_usage k
                JOIN information_schema.referential_constraints r
                  ON r.constraint_schema = k.constraint_schema
                 AND r.constraint_name = k.constraint_name
                 AND r.table_name = k.table_name
                WHERE k.constraint_schema = DATABASE()
                  AND k.referenced_table_name IS NOT NULL
                  AND k.table_name IN (
                      'company', 'job', 'job_application', 'interview', 'communication'
                  )
                ORDER BY k.table_name, k.constraint_name, k.ordinal_position
                """, (resultSet, rowNumber) -> new ForeignKeyColumn(
                        resultSet.getString("table_name"),
                        resultSet.getString("constraint_name"),
                        resultSet.getString("column_name"),
                        resultSet.getString("referenced_table_name"),
                        resultSet.getString("referenced_column_name"),
                        resultSet.getInt("ordinal_position"),
                        resultSet.getString("update_rule"),
                        resultSet.getString("delete_rule")));

        assertEquals(expectedForeignKeys(), new HashSet<>(foreignKeys));
        assertEquals(expectedForeignKeys().size(), foreignKeys.size());
    }

    @Test
    void shouldPassTrustedUserIdToMapperAndIsolateOtherUserCompany() {
        long currentUserId = currentUserProvider.currentUserId();
        long otherUserId = currentUserId == 9_000_002L ? 9_000_003L : 9_000_002L;
        long currentCompanyId = 9_010_001L;
        long otherCompanyId = 9_010_002L;

        userIsolationTestMapper.insertUser(currentUserId);
        userIsolationTestMapper.insertUser(otherUserId);
        userIsolationTestMapper.insertCompany(currentCompanyId, currentUserId);
        userIsolationTestMapper.insertCompany(otherCompanyId, otherUserId);

        assertEquals(currentUserId, currentUserService.requireCurrentUserId());
        assertEquals(currentCompanyId,
                userIsolationTestMapper.findCompanyIdForUser(currentCompanyId, currentUserId));
        assertNull(userIsolationTestMapper.findCompanyIdForUser(otherCompanyId, currentUserId));
    }

    @Test
    void shouldRejectJobLinkedToAnotherUsersCompany() {
        long userA = 9_100_001L;
        long userB = 9_100_002L;
        long userBCompany = 9_110_001L;
        long invalidJob = 9_120_001L;

        userIsolationTestMapper.insertUser(userA);
        userIsolationTestMapper.insertUser(userB);
        userIsolationTestMapper.insertCompany(userBCompany, userB);

        assertThrows(DataIntegrityViolationException.class,
                () -> userIsolationTestMapper.insertJob(invalidJob, userA, userBCompany));
    }

    @Test
    void shouldRejectJobApplicationLinkedToAnotherUsersJob() {
        long userA = 9_200_001L;
        long userB = 9_200_002L;
        long userBCompany = 9_210_001L;
        long userBJob = 9_220_001L;
        long invalidApplication = 9_230_001L;

        userIsolationTestMapper.insertUser(userA);
        userIsolationTestMapper.insertUser(userB);
        userIsolationTestMapper.insertCompany(userBCompany, userB);
        userIsolationTestMapper.insertJob(userBJob, userB, userBCompany);

        assertThrows(DataIntegrityViolationException.class,
                () -> userIsolationTestMapper.insertJobApplication(invalidApplication, userA, userBJob));
    }

    @Test
    void shouldNotRepeatVersionOneMigration() {
        assertEquals(0, flyway.migrate().migrationsExecuted);
        assertEquals(1, successfulVersionOneMigrationCount());

        List<String> businessTables = loadSchemaTables().stream()
                .filter(EXPECTED_BUSINESS_TABLES::contains)
                .toList();
        assertEquals(EXPECTED_BUSINESS_TABLES, new HashSet<>(businessTables));
        assertEquals(EXPECTED_BUSINESS_TABLES.size(), businessTables.size());
    }

    private List<String> loadSchemaTables() {
        return jdbcTemplate.queryForList("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_type = 'BASE TABLE'
                ORDER BY table_name
                """, String.class);
    }

    private int successfulVersionOneMigrationCount() {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE version = '1'
                  AND success = TRUE
                """, Integer.class);
        return count == null ? 0 : count;
    }

    private void assertColumnMetadata(ColumnMetadata column) {
        assertEquals("NO", column.nullable(), () -> "Column must be NOT NULL: " + column.qualifiedName());

        if ("id".equals(column.columnName()) || column.columnName().endsWith("_id")) {
            assertEquals("bigint", column.dataType(), column::qualifiedName);
            assertEquals("bigint unsigned", column.columnType().toLowerCase(Locale.ROOT), column::qualifiedName);
            assertNull(column.defaultValue(), column::qualifiedName);
            assertNull(column.datetimePrecision(), column::qualifiedName);
            if ("id".equals(column.columnName())) {
                assertTrue(column.extra().toLowerCase(Locale.ROOT).contains("auto_increment"),
                        column::qualifiedName);
            } else {
                assertFalse(column.extra().toLowerCase(Locale.ROOT).contains("auto_increment"),
                        column::qualifiedName);
            }
            return;
        }

        assertEquals("datetime", column.dataType(), column::qualifiedName);
        assertEquals("datetime(6)", column.columnType().toLowerCase(Locale.ROOT), column::qualifiedName);
        assertEquals(6, column.datetimePrecision(), column::qualifiedName);
        assertEquals("current_timestamp(6)", column.defaultValue().toLowerCase(Locale.ROOT),
                column::qualifiedName);
        if ("updated_at".equals(column.columnName())) {
            assertTrue(column.extra().toLowerCase(Locale.ROOT).contains("on update current_timestamp(6)"),
                    column::qualifiedName);
        } else {
            assertFalse(column.extra().toLowerCase(Locale.ROOT).contains("on update"),
                    column::qualifiedName);
        }
    }

    private void assertPrimaryIndex(Set<IndexColumn> indexes, String tableName, String logicalConstraintName) {
        assertTrue(indexes.contains(new IndexColumn(tableName, "PRIMARY", "id", 1, true)),
                () -> "Missing " + logicalConstraintName + " primary index for " + tableName);
    }

    private static ColumnMetadata mapColumnMetadata(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ColumnMetadata(
                resultSet.getString("table_name"),
                resultSet.getString("column_name"),
                resultSet.getString("data_type"),
                resultSet.getString("column_type"),
                resultSet.getString("is_nullable"),
                resultSet.getString("column_default"),
                resultSet.getString("extra"),
                nullableInteger(resultSet.getObject("datetime_precision")));
    }

    static Integer nullableInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("JDBC metadata value must be numeric");
        }
        return number.intValue();
    }

    private static Set<IndexColumn> expectedIndexes() {
        return Set.of(
                index("app_user", "PRIMARY", "id", 1, true),
                index("company", "PRIMARY", "id", 1, true),
                index("company", "uk_company_id_user", "id", 1, true),
                index("company", "uk_company_id_user", "user_id", 2, true),
                index("company", "idx_company_user_id", "user_id", 1, false),
                index("company", "idx_company_user_id", "id", 2, false),
                index("job", "PRIMARY", "id", 1, true),
                index("job", "uk_job_id_user", "id", 1, true),
                index("job", "uk_job_id_user", "user_id", 2, true),
                index("job", "idx_job_user_id", "user_id", 1, false),
                index("job", "idx_job_user_id", "id", 2, false),
                index("job", "idx_job_company_user", "company_id", 1, false),
                index("job", "idx_job_company_user", "user_id", 2, false),
                index("job_application", "PRIMARY", "id", 1, true),
                index("job_application", "idx_job_application_user_id", "user_id", 1, false),
                index("job_application", "idx_job_application_user_id", "id", 2, false),
                index("job_application", "idx_job_application_job_user", "job_id", 1, false),
                index("job_application", "idx_job_application_job_user", "user_id", 2, false),
                index("interview", "PRIMARY", "id", 1, true),
                index("interview", "idx_interview_application", "application_id", 1, false),
                index("interview", "idx_interview_application", "id", 2, false),
                index("communication", "PRIMARY", "id", 1, true),
                index("communication", "idx_communication_application", "application_id", 1, false),
                index("communication", "idx_communication_application", "id", 2, false));
    }

    private static Set<ForeignKeyColumn> expectedForeignKeys() {
        return Set.of(
                foreignKey("company", "fk_company_user", "user_id", "app_user", "id", 1),
                foreignKey("job", "fk_job_user", "user_id", "app_user", "id", 1),
                foreignKey("job", "fk_job_company_user", "company_id", "company", "id", 1),
                foreignKey("job", "fk_job_company_user", "user_id", "company", "user_id", 2),
                foreignKey("job_application", "fk_job_application_user", "user_id", "app_user", "id", 1),
                foreignKey("job_application", "fk_job_application_job_user", "job_id", "job", "id", 1),
                foreignKey("job_application", "fk_job_application_job_user", "user_id", "job", "user_id", 2),
                foreignKey("interview", "fk_interview_application", "application_id", "job_application", "id", 1),
                foreignKey("communication", "fk_communication_application", "application_id", "job_application", "id", 1));
    }

    private static IndexColumn index(
            String tableName, String indexName, String columnName, int sequence, boolean unique) {
        return new IndexColumn(tableName, indexName, columnName, sequence, unique);
    }

    private static ForeignKeyColumn foreignKey(
            String tableName,
            String constraintName,
            String columnName,
            String referencedTableName,
            String referencedColumnName,
            int ordinalPosition) {
        return new ForeignKeyColumn(
                tableName,
                constraintName,
                columnName,
                referencedTableName,
                referencedColumnName,
                ordinalPosition,
                "RESTRICT",
                "RESTRICT");
    }

    private record ColumnMetadata(
            String tableName,
            String columnName,
            String dataType,
            String columnType,
            String nullable,
            String defaultValue,
            String extra,
            Integer datetimePrecision) {

        private String qualifiedName() {
            return tableName + "." + columnName;
        }
    }

    private record IndexColumn(
            String tableName,
            String indexName,
            String columnName,
            int sequence,
            boolean unique) {
    }

    private record ForeignKeyColumn(
            String tableName,
            String constraintName,
            String columnName,
            String referencedTableName,
            String referencedColumnName,
            int ordinalPosition,
            String updateRule,
            String deleteRule) {
    }
}
