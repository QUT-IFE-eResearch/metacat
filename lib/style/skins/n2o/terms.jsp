<!DOCTYPE HTML>
<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%@ include file="session_vars.jsp"%>
<html>
<head>
  <jsp:include page="include_head.jsp">
    <jsp:param name="title" value="N2O Repository - Terms & Conditions"/>
  </jsp:include>
</head>
<!--[if lt IE 8 ]>
<body class="ie">
<![endif]-->
<!--[if (gt IE 7)|!(IE)]><!-->
<body class="">
<!--<![endif]-->
  <div class="centerContainer">
  
    <jsp:include page="include_header.jsp" />
    
    <div class="mainContent">
        <jsp:include page="include_side.jsp" />
        <div class="contentColumn left">
          <div id="termsArea" class="moduleArea roundedBorders bgWhiteAlpha90">
            <div class="moduleHeading">Terms and Conditions</div>
            <div class="moduleContent">
              <p>The data contained in the repository was collected by the N2O Network researchers. Accessing and using the data is governed by the following terms and conditions. The terms and conditions are based on the terms and conditions proposed for the <a href="http://knb.ecoinformatics.org/software/eml/">Ecological Metadata Language</a></p>
              <p><strong>Restricted access and data use</strong></p>
              <p>Use of the Dataset will be restricted to academic, research, educational, government, recreational, or other not-for-profit professional purposes. The Data User is permitted to produce and distribute derived works from this dataset provided that they are released under the same license terms as those accompanying this dataset. Any other uses for the Dataset or its derived products will require explicit permission from the Dataset Owner.</p>
              <p>The data are provided for use by the Data User. The metadata and this licence must accompany all copies made and be available to all users of this Dataset. The Data User will not redistribute the original Dataset beyond this collaboration sphere.</p>
              <p><strong>Acknowledgements and notifications</strong></p>
              <p>The Data User should acknowledge any institutional support or specific funding awards referenced in the metadata accompanying this dataset in any publications where the Dataset contributed significantly to its content. Acknowledgements should identify the supporting party, the party that received the support, and any identifying information such as grant numbers.</p>
              <p>The Data User will notify the Dataset Contact when any derivative work or publication based on or derived from the Dataset is distributed. The Data User will provide the data contact with two reprints of any publications resulting from use of the Dataset and will provide copies, or on-line access to, any derived digital products. Notification will include an explanation of how the Dataset was used to produce the derived work.</p>
              <p><strong>Co-authorship and citation</strong></p>
              <p>The Dataset has been released in the spirit of open scientific collaboration. Data Users are thus strongly encouraged to consider consultation, collaboration and/or co-authorship with the Dataset Creator.</p>
              <p>It is considered a matter of professional ethics to acknowledge the work of other scientists. Thus, the Data User will properly cite the Dataset in any publications or in the metadata of any derived data products that were produced using the Dataset. Citation should take the following general form: Creator, Year of Data Publication, Title of Dataset, Publisher, Dataset identifier.</p>
              <p><strong>Indemnity and disclaimer</strong></p>
              <p>While substantial efforts are made to ensure the accuracy of data and documentation contained in this Dataset, complete accuracy of data and metadata cannot be guaranteed. All data and metadata are made available "as is". The Data User holds all parties involved in the production or distribution of the Dataset harmless for damages resulting from its use or interpretation.</p>
              <p>By accepting this Dataset, the Data User agrees to abide by the terms of this agreement. The Data Owner shall have the right to terminate this agreement immediately by written notice upon the Data User's breach of, or non-compliance with, any of its terms. The Data User may be held responsible for any misuse that is caused or encouraged by the Data User's failure to abide by the terms of this agreement.</p>
            </div>
          </div>
        </div> <!-- contentColumn -->
    </div> <!-- mainContent -->
    <jsp:include page="include_footer.jsp" />
  </div> <!-- centerContainer -->
</body>
</html>