package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.RoleRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleService roleService;

        @PostMapping("/role")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
        BaseResponse response = new BaseResponse();
        response.setMessage("Role created");
        response.setCode(200);
        response.setData(roleService.createRole(roleRequest));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role")
    public ResponseEntity<?> getAllRole() {
        BaseResponse response = new BaseResponse();
        response.setMessage("List of Roles :");
        response.setCode(200);
        response.setData(roleService.getAllRole());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/role/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable int roleId, @RequestBody RoleRequest roleRequest) {
        roleService.updateRole(roleId, roleRequest);
        BaseResponse response = new BaseResponse();
        response.setMessage("Role updated");
        response.setCode(200);
        response.setData(roleService.updateRole(roleId, roleRequest));
        return ResponseEntity.ok(response);


    }

    @DeleteMapping("/role/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable int roleId) {
        roleService.deleteRole(roleId);
        BaseResponse response = new BaseResponse();
        response.setMessage("Role deleted");
        response.setCode(200);
        return ResponseEntity.ok(response);

    }
}
