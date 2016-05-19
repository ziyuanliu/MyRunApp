package com.example.ziyuanliu.myapplication.backend;

/**
 * Created by ziyuanliu on 5/17/16.
 */

import com.example.ziyuanliu.myapplication.backend.data.ExerciseEntryDataStore;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String id = req.getParameter("id");
        ExerciseEntryDataStore.delete(id);

        MessagingEndpoint msg = new MessagingEndpoint();
        msg.sendMessage(id);
        resp.sendRedirect("/sync.do");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
