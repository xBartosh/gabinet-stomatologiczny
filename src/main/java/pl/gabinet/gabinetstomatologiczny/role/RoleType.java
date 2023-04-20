package pl.gabinet.gabinetstomatologiczny.role;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum RoleType {
    @FieldNameConstants.Include PATIENT,
    @FieldNameConstants.Include DOCTOR,
    @FieldNameConstants.Include ADMIN;
}
