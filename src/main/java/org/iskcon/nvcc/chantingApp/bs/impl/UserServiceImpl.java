/**
 * 
 */
package org.iskcon.nvcc.chantingApp.bs.impl;

import java.util.Date;

import org.iskcon.nvcc.chantingApp.bs.UserService;
import org.iskcon.nvcc.chantingApp.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.journaldev.dao.PersonDAO;
import com.journaldev.model.Person;

/**
 * @author aditya.anand
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private PersonDAO dao;

	/* (non-Javadoc)
	 * @see org.iskcon.nvcc.chantingApp.bs.UserService#registerUser(org.iskcon.nvcc.chantingApp.dto.UserDTO)
	 */
	@Transactional
	public UserDTO registerUser(UserDTO userDto) {
		UserDTO user = new UserDTO();
		user.setUserId(userDto.getUserId());
		user.setName(userDto.getName() + "through service");
		user.setCreatedDate(new Date());
		Person person = new Person();
		person.setName(user.getName());
		dao.saveTxCheck();
		dao.save(person);
		return user;
	}

}
