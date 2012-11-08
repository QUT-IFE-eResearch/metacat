<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%
String sessionId = request.getParameter("sessionId");
%>
             <!--<div class="sectionHeader">Search for data</div>-->
            <div class="searchHeader">
            <p> 
			Please login to search through the complete data catalogue. You can search without being logged into your account, but will have access only to "public" data.
              </p>
              <p>
                Enter a search phrase (e.g. biodiversity) to search for data sets in the data catalog, or simply browse by category using the links below.
              </p>
              
              </div>
              <div class="searchArea">
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
                <div  class="searchcat">
                  <input type="text" name="searchBox" size="60" maxlength="140" id="searchBox" value=""/>
                  <!--<input type="submit" value="Search Data Catalogue"/>-->
                  <button value="Search Data Catalog" type="submit">Search Data Catalogue</button>
                </div>
                <div valign="baseline" class="clear">
                  <label class="text_plain"><input name="search" type="radio" value="quick" checked="" /> Search Title, Abstract, Keywords, Personnel (Quicker)</label>
                  <br/>
                  <label class="text_plain"><input name="search" type="radio" value="all" /> Search all fields (Slower)</label>
                </div>
              </form>
              </div>
              <!--<div><a href="javascript_required.html" onclick="document.searchForm.submit(); return false;">Browse ALL datasets available</a></div>-->

