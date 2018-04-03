package com.springbatch.csv.mysql.steps;


import org.springframework.batch.item.ItemProcessor;


import com.springbatch.csv.mysql.model.User;

public class UserItemProcessor implements ItemProcessor<User, User>{

	public User process(User user) throws Exception {
		// TODO Auto-generated method stub
		User newUser = new User();
		newUser.setName(user.getName().toUpperCase());
		return newUser;
		
	}
	
	
}
