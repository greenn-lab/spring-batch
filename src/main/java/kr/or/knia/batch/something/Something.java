package kr.or.knia.batch.something;

import kr.or.knia.batch.annotation.FixedRange;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "TB_SOMETHING")
@Data
public class Something extends CommonPart {

  public static List<Field> getFields(Class<?> clazz, List<Field> fields) {
    if (fields == null) {
      fields = new ArrayList<>();
    }

    final Class<?> superclass = clazz.getSuperclass();
    if (superclass != null) {
      getFields(superclass, fields);
    }
    else {
      fields.addAll(Arrays.asList(clazz.getFields()));
    }

    return fields;
  }

  public static void main(String... args) throws Exception {

    for (Field field : getFields(Something.class, null)) {
      System.out.println(field.getName());
    }
  }

  @Id
  @FixedRange(1)
  public String approvalNo;

  public String accidentType;

  public String accidentReceiptNo;

  public String victimName;

  public String victimAge;
}
