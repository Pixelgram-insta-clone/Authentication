package com.cognizant.Auth.proxy;

import com.cognizant.Auth.model.ResponseDto;
import com.cognizant.Auth.model.User;
import com.cognizant.Auth.model.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "user-proxy", url = "${tmem-user}")
public interface UserProxy {

    @GetMapping("users/{id}")
    User getUserById(
            @PathVariable int id
    );

    @PostMapping("users")
    User registerNewUser(@RequestBody ResponseDto dto);
}
