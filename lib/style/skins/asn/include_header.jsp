<%@ page language="java" %>
<%@ include file="settings.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<div class="header">
        <div class="header-top">
                <a target="_new" href="http://www.tern.org.au">Terrestrial Ecosystem Research Network</a>
        </div>
        <div class="header-content">
          <div class="header-left"><a href="/index.php"><img src="/templates/asn/images/asn_logo_nb.png" alt="Logo"/></a></div>
          <div class="header-right">
            <div class="header-links">

<ul class="menu-top">
<li class="item-124"><a href="/">Home</a></li><li class="item-125"><a href="/index.php/contacts">Contacts</a></li><li class="item-127"><a href="/index.php/help">Help</a></li><li class="item-126"><a href="https://www.facebook.com/TerrestrialEcosystemResearchNetwork"><img alt="Follow ASN and TERN News" src="/templates/asn/images/f_logo.png"/><span class="image-title">Follow ASN and TERN News</span> </a></li></ul>

            </div>
            <div class="login clear">
              <h3><em id="loginStatus" class="loginStatus"> &#160; </em></h3>
              <form id="loginform" name="loginform" method="post" action="auth.jsp" onSubmit="return allowLoginSubmit(this);">

                <input id="username" name="username" type="text" style="width: 140px;" onfocus="if(this.value=='username') this.value='';" onblur="if(this.value=='') this.value='username'" value="username" />
               &#160;
                <input type="hidden" name="organization" value="<%=ORGANIZATION%>" />
               &#160;
                <input id="password" name="password" type="password" maxlength="50" style="width:140px;" onfocus="if(this.value=='password') this.value='';" onblur="if(this.value=='') this.value='password'" value="password"  />
                &#160;
               &#160;
                <input type="submit" name="q" value="<%=LOGIN_LABEL%>" class="button_login" />
              </form>
            </div>
          </div>
            <div class="clear"></div>
        </div>
      </div>
