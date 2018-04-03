package com.springbatch.csv.mysql.config;



import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.springbatch.csv.mysql.jobcompletionlistener.JobCompletionListener;
import com.springbatch.csv.mysql.model.User;
import com.springbatch.csv.mysql.steps.UserItemProcessor;



@Configuration
public class BatchConfig {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	
	@Autowired
	public DataSource dataSource;

	
	//steps are the part of job
	  @Bean
	  public Step takeStep() {
		  return  stepBuilderFactory.get("takestep1").<User, User>chunk(3)
				  .reader(reader())
				  .processor(myProcessor())
				  .writer(writer())
				  .build();
	  }
	  
	  @Bean 
	  Job processJob() {
		  return jobBuilderFactory.get("processjob")
				  .incrementer(new RunIdIncrementer())
				  .listener(listener())
				  .flow(takeStep())
				  .end()
				  .build();
		  
		  }
	 
	  @Bean
	  public UserItemProcessor myProcessor() {
		  return new UserItemProcessor();
	  }
	  
	  private JobExecutionListener listener() {
			// TODO Auto-generated method stub
			return new JobCompletionListener();
			
		}
	  
	  
	  
	  @Bean
		public DataSource setDataSource() {
			
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://localhost:3306/springcsvmysql");
			dataSource.setUsername("root");
			dataSource.setPassword("root1");
			
			return dataSource;
		}
		
		@Bean 
		public JdbcBatchItemWriter<User> writer(){
			JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<User>();
			writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
			writer.setSql("insert into user(name) values(:name)");
			
			writer.setDataSource(dataSource);
			return writer;
			
		}
		
		@Bean
		public FlatFileItemReader<User> reader() {
			
			FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
			reader.setResource(new ClassPathResource("users.csv"));
			reader.setLineMapper(new DefaultLineMapper<User>() {{
				setLineTokenizer(new DelimitedLineTokenizer() {{
					setNames(new String[] {"name"});
				}});
				
				setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
					setTargetType(User.class);
				}});
			}});
			
			
			return reader;
			
		
			
		}
	  
}
	  
	  
