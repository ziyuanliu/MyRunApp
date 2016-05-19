<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="com.example.ziyuanliu.myapplication.backend.data.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Query Result</title>
</head>
<body>
	<%
		String retStr = (String) request.getAttribute("_retStr");
		if (retStr != null) {
	%>
	<%=retStr%><br>
	<%
		}
	%>

	<b>
		---------------------------------------------------------------------<br>
		<table style="width:100%">
          <tr>
            <td><b>ID</b></td>
            <td><b>INPUT TYPE</b></td>
            <td><b>ACTIVITY TYPE</b></td>
            <td><b>DATE TIME</b></td>
            <td><b>ID</b></td>
            <td><b>DURATION</b></td>
            <td><b>DISTANCE</b></td>
            <td><b>AVERAGE SPEED</b></td>
            <td><b>CALORIES</b></td>
            <td><b>CLIMB</b></td>
            <td><b>HEARTRATE</b></td>
            <td><b>COMMENT</b></td>
            <td><b>DELETE</b></td>
          </tr>

		<%
			ArrayList<ExerciseEntry> resultList = (ArrayList<ExerciseEntry>) request
					.getAttribute("result");
			if (resultList != null) {
				for (ExerciseEntry exercise : resultList) {
		%>
		        <tr>
                    <td><b><%=exercise.mId%></b></td>
                    <td><b><%=exercise.mInputType%></b></td>
                    <td><b><%=exercise.mActivityType%></b></td>
                    <td><b><%=exercise.mDateTime%></b></td>
                    <td><b><%=exercise.mDuration%></b></td>
                    <td><b><%=exercise.mDistance%></b></td>
                    <td><b><%=exercise.mAvgSpeed%></b></td>
                    <td><b><%=exercise.mCalorie%></b></td>
                    <td><b><%=exercise.mClimb%></b></td>
                    <td><b><%=exercise.mHeartRate%></b></td>
                    <td><b><%=exercise.mComment%></b></td>
                    <td><b><a href="/delete.do?id=<%=exercise.mId%>">delete</a></b></td>

                  </tr>

		<%
 	            }
 	        }
 %>
         </table>
</body>
</html>