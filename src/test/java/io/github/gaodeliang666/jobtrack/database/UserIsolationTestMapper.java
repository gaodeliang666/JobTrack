package io.github.gaodeliang666.jobtrack.database;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserIsolationTestMapper {

    void insertUser(@Param("userId") long userId);

    void insertCompany(@Param("companyId") long companyId, @Param("userId") long userId);

    void insertJob(@Param("jobId") long jobId,
            @Param("userId") long userId,
            @Param("companyId") long companyId);

    void insertJobApplication(@Param("applicationId") long applicationId,
            @Param("userId") long userId,
            @Param("jobId") long jobId);

    Long findCompanyIdForUser(@Param("companyId") long companyId, @Param("userId") long userId);
}
