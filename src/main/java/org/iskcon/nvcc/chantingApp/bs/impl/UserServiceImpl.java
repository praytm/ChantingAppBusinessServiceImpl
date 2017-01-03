/**
 * 
 */
package org.iskcon.nvcc.chantingApp.bs.impl;

import java.util.Date;

import org.iskcon.nvcc.chantingApp.bs.UserService;
import org.iskcon.nvcc.chantingApp.dto.UserDTO;
import org.springframework.stereotype.Service;

/**
 * @author aditya.anand
 *
 */
@Service
public class UserServiceImpl implements UserService {

	/* (non-Javadoc)
	 * @see org.iskcon.nvcc.chantingApp.bs.UserService#registerUser(org.iskcon.nvcc.chantingApp.dto.UserDTO)
	 */
	public UserDTO registerUser(UserDTO userDto) {
		UserDTO user = new UserDTO();
		user.setUserId(userDto.getUserId());
		user.setName(userDto.getName() + "through service");
		user.setCreatedDate(new Date());
		return user;
	}

}
