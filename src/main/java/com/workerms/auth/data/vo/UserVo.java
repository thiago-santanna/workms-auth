package com.workerms.auth.data.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserVo implements Serializable {
	
	private static final long serialVersionUID = -1718070452914969561L;

	private String userName;

	private String password;
}
