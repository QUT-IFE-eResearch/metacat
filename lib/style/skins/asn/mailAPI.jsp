<html>
<head>
<title>Mail API</title>
</head>
<body>
<table border="1" width="50%"  cellpadding="0" cellspacing="0">
  <tr>
  <td width="100%">
  <form method="POST" action="mail.jsp">
  <table border="1" width="100%" cellpadding="0" cellspacing="0">
  <h1>Mail API</h1>
  <tr>
  <td width="50%"><b>To:</b></td>
  <td width="50%"><input type="text" name="to" size="30"></td>
  </tr>
  <tr>
  <td width="50%"><b>From:</b></td>
  <td width="50%"><input type="text" name="from" size="30"></td>
  </tr>
  <tr>
  <td width="50%"><b>Subject:</b></td>
  <td width="50%"><input type="text" name="subject" size="30"></td>
  </tr>
  <tr>
  <td width="50%"><b>Description:</b></td>
  <td width="50%"><textarea name="description" type="text" 
    cols="40" rows="15" size=100>
  </textarea>
  </td>
  </tr>
  <tr>
  <td><p><input type="submit" value="Send Mail" name="sendMail"></td>
  </tr>
  </table>
  </p>
  </form>
  </td>
  </tr>
</table>
</body>
</html>