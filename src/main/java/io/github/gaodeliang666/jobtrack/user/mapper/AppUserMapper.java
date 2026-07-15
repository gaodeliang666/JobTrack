package io.github.gaodeliang666.jobtrack.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AppUserMapper {

    boolean existsById(@Param("userId") long userId);
}
