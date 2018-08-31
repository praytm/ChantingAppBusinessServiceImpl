/**
 * 
 */
package org.iskcon.nvcc.chantingApp.bs.mapper;

import java.util.Date;

import org.iskcon.nvcc.chantingApp.dao.User;
import org.iskcon.nvcc.chantingApp.dao.UserStatus;
import org.iskcon.nvcc.chantingApp.dto.UserDTO;

/**
 * @author MA041SH
 *
 */
public class UserDTOMapper {

	
	/**
	 * To map User to UserDTO without mapping password
	 * @param user
	 * @return
	 */
	public static UserDTO getUserDTO(User user){
		if(null != user){
			UserDTO userDTO = new UserDTO();
			userDTO.setCreatedDate(user.getCreationDate());
			userDTO.setEmail(user.getEmail());
			userDTO.setMobile(user.getMobile());
			userDTO.setName(user.getName());
			userDTO.setLastLoginDate(user.getLastLoginDate());
			if(null != user.getUserId()){
				userDTO.setUserId(user.getUserId().toString());	
			}			
			return userDTO;
		}
		return null;
	}
	
	/**
	 * To map UserDTO to User with option of setting UserStatus from allowed enum values
	 * @param userDto
	 * @param status
	 * @return
	 */
	public static User getUser(UserDTO userDto, UserStatus status){
		if(null != userDto){
			User user = new User();
			user.setCreationDate(new Date());
			user.setEmail(userDto.getEmail());
			user.setMobile(userDto.getMobile());
			user.setPassword(userDto.getPassword());
			user.setName(userDto.getName());
			user.setIsRegisteredViaGoogle(userDto.getIsRegisteredViaGoogle());
			user.setProfilepic(userDto.getProfilepic());
			user.setGoogleAuthToken(userDto.getGoogleAuthToken());
			if(null != status){
				user.setUserStatus(status.toString());	
			}		
			return user;	
		}
		return null;
	}
}
