package com.blackout.scheduler.blackoutscheduler.repo;

import com.blackout.scheduler.blackoutscheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {

}
