<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%
String sessionId = request.getParameter("sessionId");
%>
          <div id="searchArea" class="moduleArea roundedBorders bgWhiteAlpha90">
            <div class="moduleHeading">Search for data</div>
            <div class="moduleContent">
            <img class="left" width="94" height="80" alt="search" src="<%=SKIN_URL%>/images/search.jpg"/>
            <div id="searchAreaRight">
              <p> 
                Logging into your account enables you to search any additional, non-public data for which you may have access privileges.
              </p>
              <p>
                Enter a search phrase (e.g. biodiversity) to search for data sets in the data catalog, or simply browse by category using the links below.
              </p>
              <br/>
              <form id="searchForm" name="searchForm" method="post" action="<%=SERVLET_URL%>" target="_top" onsubmit="return validateSearchForm();">
                <input type="hidden" id="searchFormSessionId" name="sessionid" value="<%=sessionId%>" />
                <%=SIMPLE_SEARCH_METACAT_POST_FIELDS%>
                <input type="hidden" name="organizationName"/>
                <input type="hidden" name="surName"/>
                <input type="hidden" name="givenName"/>
                <input type="hidden" name="keyword"/>
                <input type="hidden" name="para"/>
                <input type="hidden" name="geographicDescription"/>
                <input type="hidden" name="literalLayout"/>
                <input type="hidden" name="abstract/para" id="abstract"/>
                <input type="hidden" name="" id="searchType" />
                <div>
                  <input type="text" name="searchBox" size="30" maxlength="200" id="searchBox" value=""/>&nbsp;&nbsp;
                  <input type="submit" value="Search Data Catalog"/>
                </div>
                <br/>
                <div>
                  <label class="text_plain"><input name="search" type="radio" value="quick" checked="" /> Search Title, Abstract, Keywords, Personnel (Quicker)</label>
                  <br/>
                  <label class="text_plain"><input name="search" type="radio" value="all" /> Search all fields (Slower)</label>
                </div>
              </form>
            </div>
            <div id="searchAreaBottom" class="clear">
              <a href="#" onclick="if (document.forms['searchForm'].onsubmit()) document.forms['searchForm'].submit();">Browse all available records</a>
            </div>
            </div> <!-- moduleContent -->
          </div> <!-- searchArea -->
