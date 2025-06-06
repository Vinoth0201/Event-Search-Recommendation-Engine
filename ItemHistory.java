package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBConnection conn = DBConnectionFactory.getDBConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		// String user_id = request.getParameter("user_id");
		if (session.getAttribute("user") == null) {
			response.setStatus(403);
			return;
		}

		String userId = session.getAttribute("user").toString();
		// String userId = request.getParameter("user_id");
		Set<Item> items = conn.getFavoriteItems(userId);
		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			try {
				obj.append("favorite", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("user") == null) {
				response.setStatus(403);
				return;
			}

			String userId = session.getAttribute("user").toString();
			JSONObject input = RpcHelper.readJsonObject(request);
			// String userId = input.getString("user_id");
			JSONArray array = (JSONArray) input.get("favorite");

			List<String> histories = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				String itemId = (String) array.get(i);
				histories.add(itemId);
			}
			conn.setFavoriteItems(userId, histories);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Is this safe enough? NO!!! We only checked whether the user has logged in, but did not check who logged in.(Hint: we should use the user id in the session attribute instead of in the url.)

			HttpSession session = request.getSession();
			if (session.getAttribute("user") == null) {
				response.setStatus(403);
				return;
			}

			String userId = session.getAttribute("user").toString();
			JSONObject input = RpcHelper.readJsonObject(request);
			// String userId = input.getString("user_id");
			JSONArray array = (JSONArray) input.get("favorite");

			List<String> histories = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				String itemId = (String) array.get(i);
				histories.add(itemId);
			}
			conn.unsetFavoriteItems(userId, histories); // the only difference between doPost and doDelete is here
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
