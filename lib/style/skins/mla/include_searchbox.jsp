<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%
String sessionId = request.getParameter("sessionId");
%>
            <div id="searchArea">
              <div class="sectionHeader">Search for data</div>
              <p> 
                Logging into your account enables you to search any additional, non-public data for which you may have access privileges.
              </p>
              <p>
                Enter a search phrase (e.g. biodiversity) to search for data sets in the data catalog, or simply browse by category using the links below.
              </p>
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
                <div>
                  <label class="text_plain"><input name="search" type="radio" value="quick" checked="" /> Search Title, Abstract, Keywords, Personnel (Quicker)</label>
                  <br/>
                  <label class="text_plain"><input name="search" type="radio" value="all" /> Search all fields (Slower)</label>
                </div>
              </form>
              <!--<div><a href="javascript_required.html" onclick="document.searchForm.submit(); return false;">Browse ALL datasets available</a></div>-->
            </div> <!--searchArea-->
