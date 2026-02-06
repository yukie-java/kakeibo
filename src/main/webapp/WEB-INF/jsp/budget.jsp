<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>予算登録</title>
</head>
<body>

<h2>月予算登録</h2>

<form action="BudgetServlet" method="post">

  月：
  <input type="month" name="targetMonth">

  金額：
  <input type="number" name="amount">

  <button class="btn">保存</button>

</form>


</body>
</html>
