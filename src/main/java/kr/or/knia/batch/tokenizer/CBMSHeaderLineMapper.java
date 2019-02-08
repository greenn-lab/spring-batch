package kr.or.knia.batch.tokenizer;

import org.springframework.batch.item.file.transform.*;
import org.springframework.stereotype.Component;

@Component
public class CBMSHeaderLineMapper extends FixedLengthTokenizer {
  public CBMSHeaderLineMapper() {
    this.setNames(new String[]{
        "type",
        "count"
    });
    this.setColumns(new Range[]{
        new Range(1, 6),
        new Range(7, 14)
    });
  }
}
