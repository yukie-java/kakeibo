<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Expense" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>家計簿</title>

<link rel="stylesheet" href="/kakeibo/css/style.css">

</head>
<body>

<!-- ===== 上部ヘッダー ===== -->
<div class="top-area">

  <h2 class="welcome">
    ようこそ、<%= session.getAttribute("userId") %>さん
  </h2>

  <div class="top-buttons">
    <form action="CsvServlet" method="get">
      <button class="btn btn-primary">CSV</button>
    </form>

    <form action="BudgetServlet" method="get">
      <button class="btn btn-warning">予算変更</button>
    </form>

    <form action="LogoutServlet" method="post">
      <button class="btn btn-secondary">ログアウト</button>
    </form>
  </div>

  <% if (Boolean.TRUE.equals(request.getAttribute("needBudgetAlert"))) { %>
    <div class="alert alert-warning">
      ⚠ 今月の予算が未登録です
      <a href="BudgetServlet">登録する</a>
    </div>
  <% } %>

</div>


<!-- ===== 支出登録 ===== -->
<h3>支出登録</h3>

<form action="/kakeibo/MainServlet" method="post">

  日付：<input type="date" name="date"><br>

  カテゴリ：
  <select name="category">
    <option value="食費">食費</option>
    <option value="交通費">交通費</option>
    <option value="日用品">日用品</option>
    <option value="その他">その他</option>
  </select><br>

  金額：<input type="number" name="amount"><br>
  メモ：<input type="text" name="memo"><br>

  <input type="submit" value="登録" class="btn">

</form>


<!-- ===== 一覧 ===== -->
<h3>一覧</h3>

<table border="1">

<tr>
  <th>日付</th>
  <th>カテゴリ</th>
  <th>金額</th>
  <th>メモ</th>
  <th></th>
</tr>

<%
List<Expense> list = (List<Expense>) request.getAttribute("expenseList");

if (list != null) {
  for (Expense e : list) {
%>

<tr>
  <td><%= e.getDate() %></td>
  <td><%= e.getCategory() %></td>
  <td><%= e.getAmount() %></td>
  <td><%= e.getMemo() %></td>

  <td>
    <form action="DeleteServlet" method="post">
      <input type="hidden" name="id" value="<%= e.getId() %>">
      <input type="submit" value="削除" class="btn">
    </form>
  </td>
</tr>

<%
  }
}
%>

</table>


<!-- ===== 合計 + 予算計算 ===== -->
<%
Object totalObj = request.getAttribute("total");
String totalStr = (totalObj == null) ? "0" : totalObj.toString();

Integer prevBudget =
    (Integer) request.getAttribute("prevBudget");

Integer currentBudget =
    (Integer) request.getAttribute("currentBudget");

Integer nextBudget =
    (Integer) request.getAttribute("nextBudget");

Double rate =
    (Double) request.getAttribute("budgetRate");

int percent =
    (rate == null) ? 0 : (int)Math.min(rate, 100);

String barColor =
    (percent >= 100) ? "#e53935" : "#1f4fd8";
%>


<!-- ===== 合計カード ===== -->
<div class="card">
  <h3>合計金額</h3>
  <div class="total-value">
    <%= totalStr %> 円
  </div>
</div>


<!-- ===== 予算カード群 ===== -->
<div style="display:flex; gap:12px; flex-wrap:wrap;">

<!-- 先月 -->
<div class="card">
  <h3>先月（<%= request.getAttribute("prevMonth") %>）</h3>
  <div class="total-value">
    <%= prevBudget == null ? "未登録" : prevBudget + " 円" %>
  </div>
</div>


<!-- 今月 -->
<div class="card">
  <h3>今月（<%= request.getAttribute("currentMonth") %>）</h3>

  <div class="total-value">
    <%= currentBudget == null ? "未登録" : currentBudget + " 円" %>
  </div>

  <!-- 達成率バー -->
  <div style="
      width:100%;
      height:20px;
      background:#eee;
      border-radius:12px;
      overflow:hidden;
      margin-top:10px;
  ">
    <div style="
        width:<%= percent %>%;
        height:100%;
        background:<%= barColor %>;
        transition:0.4s;
    ">
    </div>
  </div>

  <div style="margin-top:6px;">
    使用率：<%= percent %> %
  </div>

</div>


<!-- 来月 -->
<div class="card">
  <h3>来月（<%= request.getAttribute("nextMonth") %>）</h3>
  <div class="total-value">
    <%= nextBudget == null ? "未登録" : nextBudget + " 円" %>
  </div>
</div>

</div>


<!-- ===== CSV ===== -->
<h3>CSVダウンロード（条件指定）</h3>

<form action="CsvServlet" method="get">

  月：
  <input type="month" name="month">

  カテゴリ：
  <select name="category">
    <option value="">全カテゴリ</option>
    <option value="食費">食費</option>
    <option value="交通費">交通費</option>
    <option value="日用品">日用品</option>
    <option value="その他">その他</option>
  </select>

  <input type="submit" value="CSV出力" class="btn">

</form>


<!-- ===== カテゴリ別合計 ===== -->
<h4>カテゴリ別合計</h4>

<ul>
<%
Map<String,Integer> categoryMap =
    (Map<String,Integer>) request.getAttribute("categorySum");

if (categoryMap != null) {
  for (String key : categoryMap.keySet()) {
%>
  <li><%= key %>：<%= categoryMap.get(key) %> 円</li>
<%
  }
}
%>
</ul>


<!-- ===== 月別合計 ===== -->
<h4>月別合計</h4>

<ul>
<%
Map<String,Integer> monthMap =
    (Map<String,Integer>) request.getAttribute("monthSum");

if (monthMap != null) {
  for (String key : monthMap.keySet()) {
%>
  <li><%= key %>：<%= monthMap.get(key) %> 円</li>
<%
  }
}
%>
</ul>


</body>
</html>
