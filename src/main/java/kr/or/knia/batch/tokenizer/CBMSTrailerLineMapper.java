package kr.or.knia.batch.tokenizer;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.stereotype.Component;

@Component
public class CBMSTrailerLineMapper extends FixedLengthTokenizer {
  public CBMSTrailerLineMapper() {
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
