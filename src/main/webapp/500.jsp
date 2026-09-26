<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Something went wrong — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script>(function(){try{var t=localStorage.getItem("adharshmart-theme");if(t==="light"||t==="dark")document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
</head>
<body>
<main class="error-page">
    <p class="error-code">500</p>
    <h1>Something went wrong on our end.</h1>
    <p>No details are shown here by design — our team has been notified. Please try again shortly.</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/index.jsp">Return home</a>
</main>
</body>
</html>
