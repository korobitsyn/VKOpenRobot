package com.alkor.vph.vk;

import com.alkor.vph.vk.entities.*;
import com.sun.deploy.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 01.07.13
 * Time: 14:14
 */
public interface VKConnector {

    List<Group> searchGroups(String query, int count, String token) throws IOException;

    List<User> getUsers(Collection<Long> userIds, String token) throws IOException;

    List<Long> getGroupMembers(long groupId, int count, String token) throws IOException;

    void joinGroup(long groupId, String token) throws IOException;

    AddFriendResult addFriend(long uid, String text, Captcha captcha, String token) throws IOException;

    WallPostResult wallPost(Long gid, Post post, Captcha captcha, String token) throws IOException;

    String getMyWallUploadServer(String token) throws IOException;

    UploadedPhoto uploadPhoto(String serverUrl, byte[] photo) throws IOException;

    String saveMyWallPhoto(UploadedPhoto uploadedPhoto, String token) throws IOException;

    AddFriendResult setStatus(String text, String token) throws IOException;

}
