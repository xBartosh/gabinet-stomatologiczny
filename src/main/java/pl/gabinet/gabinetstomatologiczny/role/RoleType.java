package pl.gabinet.gabinetstomatologiczny.role;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum RoleType {
    @FieldNameConstants.Include ROLE_PATIENT,
    @FieldNameConstants.Include ROLE_DOCTOR,
    @FieldNameConstants.Include ROLE_ADMIN;

    public static RoleType getRoleType(String roleName) {
        roleName = roleName.toUpperCase();
        return switch (roleName){
            case "ROLE_DOCTOR" -> ROLE_DOCTOR;
            case "ROLE_PATIENT" -> ROLE_PATIENT;
            case "ROLE_ADMIN" -> ROLE_ADMIN;
            default -> ROLE_PATIENT;
        };
    }
}
