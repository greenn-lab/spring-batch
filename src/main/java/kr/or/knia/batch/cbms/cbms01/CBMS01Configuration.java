package kr.or.knia.batch.cbms.cbms01;

import kr.or.knia.batch.cbms.CBMS;
import kr.or.knia.batch.cbms.tokenizer.CBMSHeaderLineMapper;
import kr.or.knia.batch.cbms.tokenizer.CBMSTrailerLineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CBMS01Configuration {
  public static final String NAME = "CBMS01";

  private final JobBuilderFactory jobBuilder;
  private final StepBuilderFactory stepBuilder;
  private final EntityManagerFactory entityManagerFactory;

  @Value("${cbms.target.path}")
  private String path;

  @Bean
  public Job CBMS01() {
    return jobBuilder
        .get("CBMS01")
        .start(saveToDatabase())
        .build();
  }

  @Bean
  public Step saveToDatabase() {
    return stepBuilder
        .get("CBMS01/execute")
        .<CBMS01, CBMS01>chunk(2)
        .reader(lineByLineDataReader())
        .writer(jpaItemWriter())
        .build();
  }

  @Bean
  public JpaItemWriter<CBMS01> jpaItemWriter() {
    JpaItemWriter<CBMS01> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(entityManagerFactory);
    return writer;
  }

  @Bean
  public FlatFileItemReader<CBMS01> lineByLineDataReader() {
    BeanWrapperFieldSetMapper<CBMS01> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(CBMS01.class);

    DefaultLineMapper<CBMS01> lineMapper = new DefaultLineMapper<>();
    lineMapper.setFieldSetMapper(fieldSetMapper);

    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames(new String[]{
        "transferCode",
        "dataType",
        "serialNo",
        "companyCode",
        "approvalNo",
        "accidentType",
        "accidentReceiptNo",
        "victimName",
        "victimAge"
    });
    tokenizer.setColumns(new Range[]{
        new Range(1, 6),
        new Range(7, 13),
        new Range(14, 29),
        new Range(30, 39),
        new Range(40, 47),
        new Range(48, 50)
    });

    lineMapper.setLineTokenizer(tokenizer);

    final CBMSHeaderLineMapper headerLineMapper = new CBMSHeaderLineMapper();
    final BeanWrapperFieldSetMapper<CBMS.Header> headerBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
    headerBeanWrapperFieldSetMapper.setTargetType(CBMS.Header.class);
    headerBeanWrapperFieldSetMapper.mapFieldSet(headerLineMapper.tokenize())

    Map<String, LineTokenizer> tokenizers = new HashMap<>(3);
    tokenizers.put("CBMS01", headerLineMapper);
    tokenizers.put("CBMS02", tokenizer);
    tokenizers.put("CBMS03", new CBMSTrailerLineMapper());

    PatternMatchingCompositeLineMapper<CBMS01> patternMatchingCompositeLineMapper = new PatternMatchingCompositeLineMapper<>();
    patternMatchingCompositeLineMapper.setTokenizers(tokenizers);

    FlatFileItemReader<CBMS01> reader = new FlatFileItemReader<>();
    reader.setResource(new FileSystemResource(path));
    reader.setLineMapper(patternMatchingCompositeLineMapper);
    return reader;
  }
}
