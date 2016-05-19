package com.example.ziyuanliu.myapplication.backend;

/**
 * Created by ziyuanliu on 5/17/16.
 */

import com.example.ziyuanliu.myapplication.backend.data.ExerciseEntry;
import com.example.ziyuanliu.myapplication.backend.data.ExerciseEntryDataStore;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SyncServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        ArrayList<ExerciseEntry> list = ExerciseEntryDataStore.list();
        req.setAttribute("result", list);

        getServletContext().getRequestDispatcher("/query_result.jsp").forward(
                req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String json = req.getParameter("json");
        JSONArray jsonArray;
        if (json == null || json.equals("")) {
            req.setAttribute("_retStr", "invalid input");
            getServletContext().getRequestDispatcher("/query_result.jsp")
                    .forward(req, resp);
            return;
        }else{
            try {

                jsonArray = new JSONArray(json);
            } catch (org.json.JSONException e) {
                // crash and burn
                String err = "Error parsing JSON request string "+ e.toString();

                req.setAttribute("_retStr", err);
                getServletContext().getRequestDispatcher("/query_result.jsp")
                        .forward(req, resp);
                return;
            }
        }

        // lesson from cs10: hashable/hashcode is the determining factor for the unique of the exercise entry
        // we shall sync with a toRemove which will contain the difference of what is on the phone
        ArrayList<ExerciseEntry> toRemove = ExerciseEntryDataStore.list();

        for (int i = 0; i < jsonArray.length(); i++) {
            ExerciseEntry exercise = new ExerciseEntry(jsonArray.getJSONObject(i));
            Boolean removed = toRemove.remove(exercise);
            ExerciseEntryDataStore.add(exercise);

        }


        // here we remove everything that was left over from what was on the android device
        for (ExerciseEntry exercise: toRemove){
            ExerciseEntryDataStore.delete(exercise.mId);
        }

        req.setAttribute("result", ExerciseEntryDataStore.list());

        getServletContext().getRequestDispatcher("/query_result.jsp").forward(
                req, resp);
    }

}
