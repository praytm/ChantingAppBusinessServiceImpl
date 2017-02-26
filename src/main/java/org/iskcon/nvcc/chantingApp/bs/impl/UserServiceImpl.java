/**
 * 
 */
package org.iskcon.nvcc.chantingApp.bs.impl;

import java.util.Date;

import org.iskcon.nvcc.chantingApp.bs.UserService;
import org.iskcon.nvcc.chantingApp.bs.mapper.UserDTOMapper;
import org.iskcon.nvcc.chantingApp.dao.LoginDAO;
import org.iskcon.nvcc.chantingApp.dao.RegistrationDAO;
import org.iskcon.nvcc.chantingApp.dao.User;
import org.iskcon.nvcc.chantingApp.dao.UserStatisticsDAO;
import org.iskcon.nvcc.chantingApp.dao.UserStatus;
import org.iskcon.nvcc.chantingApp.dao.UserStatusDAO;
import org.iskcon.nvcc.chantingApp.dto.RefreshUserStatisticsOutputDTO;
import org.iskcon.nvcc.chantingApp.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aditya.anand
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private RegistrationDAO registrationDao;
	
	@Autowired
	private UserStatusDAO userStatusDao;

	@Autowired
	private LoginDAO loginDAO;
	
	@Autowired
	private UserStatisticsDAO userStatisticsDAO;

	@Transactional
	public UserDTO registerUser(UserDTO userDto) {
		User user = UserDTOMapper.getUser(userDto, UserStatus.REGISTERED);
		User savedUser = registrationDao.registerUser(user);
		return UserDTOMapper.getUserDTO(savedUser);

	}

	@Transactional
	public UserDTO loginUser(UserDTO userDto) {
		UserDTO outputUserDto;
		User userInput = UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		if (null == userOutput) {
			// matching record found corresponding to email id ,but password did
			// not match
			return null;
		}
		if (null != userOutput.getUserId()) {
			outputUserDto = UserDTOMapper.getUserDTO(userOutput);
			// it means there is a successful login and user instance is coming
			// from db, so update the last login time to current time
			userOutput.setLastLoginDate(new Date());
			loginDAO.updateUser(userOutput);
		} else {
			//no matching record found in db corresponding to email used for login
			outputUserDto = UserDTOMapper.getUserDTO(userOutput);
		}
		return outputUserDto;
	}

	@Transactional
	public Boolean changeUserStatusToActive(UserDTO userDto){
		User userInput = UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		if(null != userOutput && null != userOutput.getUserId()){
			return userStatusDao.changeUserStatusToActive(userOutput);
		}
		return false;
	}
	
	@Transactional
	public Boolean changeUserStatusToNotActive(UserDTO userDto){
		User userInput = UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		if(null !=userOutput && null != userOutput.getUserId()){
			return userStatusDao.changeUserStatusToNotActive(userOutput);
		}
		return false;
	}
	
	@Transactional
	public RefreshUserStatisticsOutputDTO refreshUserStatistics(UserDTO userDto){
		User userInput =  UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		RefreshUserStatisticsOutputDTO refreshUserStatisticsOutputDTO = new RefreshUserStatisticsOutputDTO();
		if(null != userOutput && null != userOutput.getUserId()){
		Integer totalNumberOfUsers = userStatisticsDAO.getNumberOfAllUsers();
		Integer totalNumberOfActiveUsers = userStatisticsDAO.getNumberOfActiveUsers();
		Integer totalNumberOfBeadsForUser = userStatisticsDAO.getTotalNumberOfBeadsForUser(userOutput);
		Integer todaysNumberOfBeadsForUser = userStatisticsDAO.getTodaysNumberOfBeadsForUser(userOutput);
		refreshUserStatisticsOutputDTO.setTodaysNumberOfBeadsForUser(todaysNumberOfBeadsForUser);
		refreshUserStatisticsOutputDTO.setTotalNumberOfActiveUsers(totalNumberOfActiveUsers);
		refreshUserStatisticsOutputDTO.setTotalNumberOfBeadsForUser(totalNumberOfBeadsForUser);
		refreshUserStatisticsOutputDTO.setTotalNumberOfUsers(totalNumberOfUsers);	
		return refreshUserStatisticsOutputDTO;
		}				
		return null;
	}
}
