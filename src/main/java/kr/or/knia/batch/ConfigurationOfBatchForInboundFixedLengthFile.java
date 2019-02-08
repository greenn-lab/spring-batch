package kr.or.knia.batch;

import kr.or.knia.batch.something.Something;
import kr.or.knia.batch.tokenizer.CBMSTrailerLineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
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
public class ConfigurationOfBatchForInboundFixedLengthFile {

  private final JobBuilderFactory jobBuilder;
  private final StepBuilderFactory stepBuilder;
  private final EntityManagerFactory entityManagerFactory;

  @Value("${batch.name}")
  private final String batchName;

  @Value("${batch.class-name}")
  private final Class<?> batchClass;

  @Value("${batch.target.path}")
  private final String path;

  @Value("${batch.target.chunk-size}")
  private final Integer chunkSize;

  @Bean
  public Job batch() throws ClassNotFoundException {
    return jobBuilder
        .get(batchName)
        .start(extractAndPersist())
        .build();
  }

  @Bean
  public Step extractAndPersist() throws ClassNotFoundException {
    final StepBuilder stepBuilder = this.stepBuilder.get(batchName + "/execute");
    final SimpleStepBuilder<Object, Object> simpleStepBuilder = stepBuilder.chunk(chunkSize)
        .reader(lineByLineDataReader())
        .writer(jpaItemWriter())
        ;

    return simpleStepBuilder.build();
  }

  @Bean
  public JpaItemWriter<Object> jpaItemWriter() {
    JpaItemWriter<Object> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(entityManagerFactory);
    return writer;
  }

  @Bean
  public FlatFileItemReader<Object> lineByLineDataReader() throws ClassNotFoundException {
    BeanWrapperFieldSetMapper<Object> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(batchClass);

    DefaultLineMapper<Object> lineMapper = new DefaultLineMapper<>();
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
        new Range(7, 8),
        new Range(9, 15),
        new Range(16, 18),
        new Range(19, 28),
        new Range(29, 30),
        new Range(31, 50),
        new Range(51, 90),
        new Range(91, 92)
    });

    lineMapper.setLineTokenizer(tokenizer);

//    Map<String, LineTokenizer> tokenizers = new HashMap<>(3);
//    tokenizers.put(batchName + "11", headerLineMapper);
//    tokenizers.put(batchName + "22", tokenizer);
//    tokenizers.put(batchName + "33", new CBMSTrailerLineMapper());
//
//    PatternMatchingCompositeLineMapper<Something> patternMatchingCompositeLineMapper = new PatternMatchingCompositeLineMapper<>();
//    patternMatchingCompositeLineMapper.setTokenizers(tokenizers);
//
//    FlatFileItemReader<Something> reader = new FlatFileItemReader<>();
//    reader.setResource(new FileSystemResource(path));
//    reader.setLineMapper(patternMatchingCompositeLineMapper);
//    return reader;
    return null;
  }
}
