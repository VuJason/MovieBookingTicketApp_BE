package com.example.cinema_booking.service;


import com.example.cinema_booking.exception.InsertException;
import com.example.cinema_booking.model.Role;
import com.example.cinema_booking.repository.RoleRepository;
import com.example.cinema_booking.dto.request.RoleRequest;
import com.example.cinema_booking.dto.response.RoleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleResponse createRole(RoleRequest roleRequest) {
        Optional<Role> existedRole = roleRepository.findByName(roleRequest.getName());
        if (existedRole.isPresent()) {
            throw new InsertException("Role already exists");
        }

        try {
            Role role = new Role();
            role.setName(roleRequest.getName());
            role = roleRepository.save(role);

            RoleResponse response = new RoleResponse();
            response.setId(role.getId());
            response.setName(role.getName());

            return response;
        } catch (Exception e){
            throw new InsertException(e.getMessage());
        }
    }

    public Role updateRole(int roleId, RoleRequest roleRequest) {
        Optional<Role> role = roleRepository.findById(roleId);
        if(role.isPresent()) {
            Role updatedRole = role.get();
            updatedRole.setName(roleRequest.getName());
            roleRepository.save(updatedRole);
            return updatedRole;
        }else {
            throw new RuntimeException("User with ID " + roleId + " not found");
        }
    }

    public List<RoleResponse> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        //return showtimeRepository.findAll();
    }

    public void deleteRole(int roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if(role.isPresent()) {
            roleRepository.delete(role.get());
        }
    }

    // Entity to DTO for List all Showtime
    private RoleResponse convertToResponseDTO(Role role) {
        RoleResponse responseDTO = new RoleResponse();
        responseDTO.setId(role.getId());
        responseDTO.setName(role.getName());
        return responseDTO;
    }
}
