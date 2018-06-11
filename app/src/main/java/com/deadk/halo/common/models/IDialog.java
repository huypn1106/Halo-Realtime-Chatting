/*******************************************************************************
 * Copyright 2016 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.deadk.halo.common.models;

import java.util.HashMap;

/**
 * For implementing by real dialog model
 */

public interface IDialog<MESSAGE extends IMessage> {

    String getDialogId();
    void setDialogId(String dialogId);

    String getDialogName();
    void setDialogName(String dialogName);

    String getUsers();
    void setUsers(String users);

    String getLastMessage();
    void setLastMessage(String message);

    int getUnreadCount();
    void setUnreadCount(int unreadCount);

    HashMap<String, Object> getIdClient();
    void  setIdClient(HashMap<String, Object> idClient);

    String getClientAvatar();
    void  setClientAvatar(String clientAvatar);

    int getIsGroup();
    void  setIsGroup(int isGroup);

    String getUidHost();
    void setUidHost(String uidHost);
}
