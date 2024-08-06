package com.xin.spark.service;

import com.xin.spark.dao.UserFollowingDao;
import com.xin.spark.domain.FollowingGroup;
import com.xin.spark.domain.User;
import com.xin.spark.domain.UserFollowing;
import com.xin.spark.domain.UserInfo;
import com.xin.spark.domain.constant.UserConstanst;
import com.xin.spark.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Transactional
    public void addUserFollowings(UserFollowing userFollowing){
        Long groupId = userFollowing.getGroupId();
        if (groupId == null){
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstanst.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null){
                throw new ConditionException("分组不存在！");
            }
        }

        Long followingId = userFollowing.getFollowingId();
        User followingUser = userService.getUserById(followingId);
        if (followingUser == null){
            throw new ConditionException("关注用户不存在！");
        }
        // 如果一个用户取关了，那么就要先删除原来的关系，然后再绑定新的关系
        userFollowingDao.delete(userFollowing.getUserId(), userFollowing.getFollowingId());
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }
    // 获取关注的用户列表
    // 根据关注用户的id查询关注用户的基本信息
    // 将关注用户按关注分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId){
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingSet = list.stream()
                .map(UserFollowing::getFollowingId)
                .collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (followingSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingSet);
        }

        for (UserFollowing userFollowing : list){
            for (UserInfo userInfo : userInfoList){
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstanst.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        for (FollowingGroup group : groupList){
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list){
                if (group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;

    }
}
