package org.wallet.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.wallet.dto.GenderType;
import org.wallet.dto.IndividualStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("individuals")
public class Individual {
  @Id
  private Long id;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private String firstName;
  private String lastName;
  private LocalDateTime dateOfBirth;
  private String passportNumber;
  private String personalIdentityNumber;
  private GenderType gender;
  private IndividualStatus status;
  private LocalDateTime verifiedAt;
  private LocalDateTime archivedAt;
  private String email;
  private String phone;
  private boolean filled;
  private UUID profileUid;
  private String applicantId;
  private Long addressId;
}
