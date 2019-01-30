package kr.or.knia.batch.cbms;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Data
public class CBMS {
  @Id
  private String transferCode;

  private String dataType;

  @Id
  private Long serialNo;

  @Id
  private String companyCode;

  @Id
  private String standardDate;

  @Transient
  private Long totalRecordCount;
}
