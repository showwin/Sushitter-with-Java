/*
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jp.go.nict.langrid.client;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jp.go.nict.langrid.commons.cs.binding.BindingNode;
import jp.go.nict.langrid.commons.cs.binding.DynamicBindingUtil;
import jp.go.nict.langrid.commons.rpc.RpcHeader;
import jp.go.nict.langrid.commons.ws.BasicAuthUtil;
import jp.go.nict.langrid.commons.ws.Constants;
import jp.go.nict.langrid.commons.ws.LangridConstants;

/**
 * 
 * 
 * @author Takao Nakaguchi
 */
public class RpcRequestAttributes implements RequestAttributes{
	public RpcRequestAttributes(){
	}

	public String getUserId() {
		return userId;
	}

	@Override
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setConnectTimeout(int timeoutMillis) {
		this.connectTimeoutMillis = timeoutMillis;
	}

	@Override
	public void setTimeout(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public void addRequestMimeHeader(String name, String value) {
		mightBeModified = true;
		httpHeaders.put(name, value);
	}

	@Override
	public void addRequestMimeHeaders(Map<String, Object> headers) {
		mightBeModified = true;
		httpHeaders.putAll(headers);
	}

	public Map<String, Object> getRequestMimeHeaders() {
		mightBeModified = true;
		return httpHeaders;
	}

	@Override
	public void addRequestRpcHeader(String namespace, String name, String value) {
		mightBeModified = true;
		rpcHeaders.put(new QName(namespace, name), value);
	}

	@Override
	public void addRequestRpcHeaders(Map<QName, Object> headers) {
		mightBeModified = true;
		rpcHeaders.putAll(headers);
	}

	@Override
	public Collection<BindingNode> getTreeBindings() {
		mightBeModified = true;
		return bindings;
	}

	public void setUpConnection(HttpURLConnection con){
		if(connectTimeoutMillis != -1) con.setConnectTimeout(connectTimeoutMillis);
		if(timeoutMillis != -1) con.setReadTimeout(timeoutMillis);
		if(userId != null){
			if(password == null) password = "";
			con.addRequestProperty(Constants.HEADER_AUTHORIZATION
					, BasicAuthUtil.encode(userId, password));
		}
		for(Map.Entry<String, Object> e : httpHeaders.entrySet()){
			con.addRequestProperty(e.getKey(), e.getValue().toString());
		}
	}

	public Collection<RpcHeader> getAllRpcHeaders(){
		if(mightBeModified){
			bindingsValue = DynamicBindingUtil.encodeTree(this.bindings);
			mightBeModified = false;
		}
		List<RpcHeader> ret = new ArrayList<RpcHeader>();
		for(Map.Entry<QName, Object> entry : rpcHeaders.entrySet()){
			ret.add(new RpcHeader(
					entry.getKey().getNamespaceURI()
					, entry.getKey().getLocalPart()
					, entry.getValue().toString()));
		}
		if(bindingsValue != null && !bindingsValue.equals("[]")){
			ret.add(new RpcHeader(
					LangridConstants.ACTOR_SERVICE_TREEBINDING, "binding", bindingsValue
					));
		}
		return ret;
	}

	private String userId;
	private String password;
	private int connectTimeoutMillis = -1;
	private int timeoutMillis = -1;
	private Map<String, Object> httpHeaders = new HashMap<String, Object>();
	private boolean mightBeModified = true;
	private List<BindingNode> bindings = new ArrayList<BindingNode>();
	private Map<QName, Object> rpcHeaders = new HashMap<QName, Object>();
	private String bindingsValue;
}
