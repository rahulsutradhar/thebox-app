package one.thebox.android.util;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;

import com.google.gson.Gson;

public class CoreGsonUtils
{

	private static Gson instance;

	private static Gson getGsonObject()
	{

		if (instance == null)
		{
			instance = new Gson();
		}
		return instance;

	}

	public static <T> T fromJson(String string, Class<T> model)
	{

		Gson gson = getGsonObject();
		T gfromat = null;
		try
		{
			gfromat = gson.fromJson(string, model);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gfromat;
	}

	public static <T> T fromJson(String string, Type type)
	{

		Gson gson = getGsonObject();
		T gfromat = null;
		try
		{
			gfromat = gson.fromJson(string, type);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gfromat;
	}

	public static <T> String toJson(Object obj)
	{

		String gsonstr = "";

		Gson gson = getGsonObject();
		gsonstr = gson.toJson(obj);
		return gsonstr;
	}

	public static <T> ArrayList<T> fromJsontoArrayList(String string, Class<T> model)
	{

		Gson gson = getGsonObject();
		T gfromat = null;
		ArrayList<T> localArrayList = new ArrayList<T>();
		try
		{

			JSONArray jsonInner = new JSONArray(string);
			int i = 0;
			while (i < jsonInner.length())
			{
				gfromat = gson.fromJson(jsonInner.get(i).toString(), model);
				localArrayList.add(gfromat);
				i++;

			}

		} catch (Exception e)

		{
			e.printStackTrace();
		}

		return localArrayList;
	}

}
