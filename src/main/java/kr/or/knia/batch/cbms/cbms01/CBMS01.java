package kr.or.knia.batch.cbms.cbms01;

import kr.or.knia.batch.cbms.CBMS;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CBMS01")
@Data
public class CBMS01 extends CBMS {

  @Id
  public String approvalNo;

  public String accidentType;

  public String accidentReceiptNo;

  public String victimName;

  public String victimAge;
}
