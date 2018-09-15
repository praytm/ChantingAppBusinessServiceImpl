/**
 * 
 */
package org.iskcon.nvcc.chantingApp.bs.impl;

import java.util.Date;
import java.util.Map;

import org.iskcon.nvcc.chantingApp.bs.UserService;
import org.iskcon.nvcc.chantingApp.bs.mapper.UserDTOMapper;
import org.iskcon.nvcc.chantingApp.dao.ChantingSessionHistory;
import org.iskcon.nvcc.chantingApp.dao.ChantingSessionHistoryDAO;
import org.iskcon.nvcc.chantingApp.dao.LoginDAO;
import org.iskcon.nvcc.chantingApp.dao.RegistrationDAO;
import org.iskcon.nvcc.chantingApp.dao.User;
import org.iskcon.nvcc.chantingApp.dao.UserStatisticsDAO;
import org.iskcon.nvcc.chantingApp.dao.UserStatus;
import org.iskcon.nvcc.chantingApp.dao.UserStatusDAO;
import org.iskcon.nvcc.chantingApp.dto.ChantingHistoryDTO;
import org.iskcon.nvcc.chantingApp.dto.ChantingSessionDTO;
import org.iskcon.nvcc.chantingApp.dto.GetChantingHistoryRequestDTO;
import org.iskcon.nvcc.chantingApp.dto.HighestChantingInfoDTO;
import org.iskcon.nvcc.chantingApp.dto.RefreshUserStatisticsOutputDTO;
import org.iskcon.nvcc.chantingApp.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@Autowired
	private ChantingSessionHistoryDAO chantingSessionHistoryDAO;

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(UserServiceImpl.class);

	@Transactional
	public Integer getTotalNumberOfBeadsForToday() {
		
	return userStatisticsDAO.getTotalNumberOfBeadsForToday();

	}
	
	@Transactional
	public String getValueFromMasterData(String key) {
		
	return userStatisticsDAO.getValueFromMasterData(key);

	}
	
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
			// no matching record found in db corresponding to email used for
			// login
			outputUserDto = UserDTOMapper.getUserDTO(userOutput);
		}
		return outputUserDto;
	}

	@Transactional
	public Boolean changeUserStatusToActive(UserDTO userDto) {
		User userInput = UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		if (null != userOutput && null != userOutput.getUserId()) {
			return userStatusDao.changeUserStatusToActive(userOutput);
		}
		return false;
	}

	@Transactional
	public Boolean changeUserStatusToNotActive(UserDTO userDto) {
		User userInput = UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		if (null != userOutput && null != userOutput.getUserId()) {
			return userStatusDao.changeUserStatusToNotActive(userOutput);
		}
		return false;
	}

	@Transactional
	public RefreshUserStatisticsOutputDTO refreshUserStatistics(UserDTO userDto) {
		User userInput = UserDTOMapper.getUser(userDto, null);
		User userOutput = loginDAO.loginUser(userInput);
		RefreshUserStatisticsOutputDTO refreshUserStatisticsOutputDTO = new RefreshUserStatisticsOutputDTO();
		if (null != userOutput && null != userOutput.getUserId()) {
			Integer totalNumberOfUsers = userStatisticsDAO
					.getNumberOfAllUsers();
			Integer totalNumberOfActiveUsers = userStatisticsDAO
					.getNumberOfActiveUsers();
			Integer totalNumberOfBeadsForUser = userStatisticsDAO
					.getTotalNumberOfBeadsForUser(userOutput);
			Integer todaysNumberOfBeadsForUser = userStatisticsDAO
					.getTodaysNumberOfBeadsForUser(userOutput, null);
			refreshUserStatisticsOutputDTO
					.setTodaysNumberOfBeadsForUser(todaysNumberOfBeadsForUser);
			refreshUserStatisticsOutputDTO
					.setTotalNumberOfActiveUsers(totalNumberOfActiveUsers);
			refreshUserStatisticsOutputDTO
					.setTotalNumberOfBeadsForUser(totalNumberOfBeadsForUser);
			refreshUserStatisticsOutputDTO
					.setTotalNumberOfUsers(totalNumberOfUsers);
			return refreshUserStatisticsOutputDTO;
		}
		return null;
	}

	@Transactional
	public Boolean saveNewChantingSession(ChantingSessionDTO chantingSessionDto) {

		User user = new User();
		user.setEmail(chantingSessionDto.getUserName());
		user.setPassword(chantingSessionDto.getPassword());
		User userOutput = loginDAO.loginUser(user);
		if (null != userOutput && null != userOutput.getUserId()) {
			ChantingSessionHistory chantingSessionHistory = new ChantingSessionHistory();
			chantingSessionHistory.setChantingSessionDate(chantingSessionDto
					.getChantingSessionDate());
			chantingSessionHistory.setChantingSessionEndTime(chantingSessionDto
					.getChantingSessionEndTime());
			chantingSessionHistory
					.setChantingSessionStartTime(chantingSessionDto
							.getChantingSessionStartTime());
			chantingSessionHistory.setNumberOfBeads(chantingSessionDto
					.getNumberOfBeads());
			chantingSessionHistory.setUser(userOutput);
			boolean saveResult = chantingSessionHistoryDAO
					.saveNewChantingSessionHistory(chantingSessionHistory);
			logger.info("ChantingSessionHistory saved :: {} ", saveResult);
			// since user session is saved now ,hence reseting status of user to
			// notActive in User table
			boolean userStatusChange = userStatusDao
					.changeUserStatusToNotActive(userOutput);
			logger.info(
					"User status changed to NotActive :: {}  for User : {}",
					userStatusChange, userOutput.getEmail());
			return true;
		}
		return false;
	}

	@Transactional
	public ChantingHistoryDTO getChantingHistoryForUser(GetChantingHistoryRequestDTO  getChantingHistoryRequestDTO) {
		logger.info("UserDTO email is : {}",getChantingHistoryRequestDTO.getUserDto().getEmail());
		User userInput = UserDTOMapper.getUser(getChantingHistoryRequestDTO.getUserDto(), null);
		User userOutput = loginDAO.loginUser(userInput);
		if (null != userOutput && null != userOutput.getUserId()) {
			Map<String, Integer> chantinghistoryMap = userStatisticsDAO
					.getChantingHistoryForUser(userOutput, getChantingHistoryRequestDTO.getDateInput());
			ChantingHistoryDTO chantingHistoryDTO = new ChantingHistoryDTO();
			chantingHistoryDTO.setChantingHistory(chantinghistoryMap);
			return chantingHistoryDTO;
		}
		return null;
	}

	public HighestChantingInfoDTO getHighestChanting() {
		// TODO Auto-generated method stub
		
		return null;
	}
	
}
