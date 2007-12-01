<div class="header">Frequently Asked Questions</div>
<div class="body">
<ul>
  <li><a href="#q1">What is sventon?</a></li>
  <li><a href="#q2">What features does sventon have?</a></li>
  <li><a href="#q3">What do I need to run sventon?</a></li>
  <li><a href="#q4">What platforms are supported?</a></li>
  <li><a href="#q5">What configurations have been tested?</a></li>
  <li><a href="#q6">How do I install and configure sventon?</a></li>
  <li><a href="#q7">How do I uninstall sventon?</a></li>
  <li><a href="#q8">How does the indexing work?</a></li>
  <li><a href="#q9">How do I upgrade sventon to a newer version?</a></li>
  <li><a href="#q10">Will sventon in any way jeopardize my Subversion repository?</a></li>
  <li><a href="#q11">Will sventon write any information to my Windows registry?</a></li>
  <li><a href="#q12">Why is not file type [XYZ] colorized when displayed in sventon?</a></li>
  <li><a href="#q13">Why is the binary file type [XYZ] treated as a text file when I view it in sventon?</a></li>
  <li><a href="#q14">Is there a log file for sventon?</a></li>
  <li><a href="#q15">Does sventon support non-US-ASCII charsets?</a></li>
  <li><a href="#q16">Can I send suggestions for new functions?</a></li>
  <li><a href="#q17">Will sventon be avaliable for CVS (or any other version control system) in the future?</a></li>
  <li><a href="#q18">What license do you use?</a></li>
  <li><a href="#q19">Why did you start this project?</a></li>
  <li><a href="#q20">Is there a publicly running version of Sventon that I can try?</a></li>
  <li><a href="#q21">Can I change the layout of the RSS feed?</a></li>
  <li><a href="#q22">Can I use sventon together with JIRA?</a></li>
  <li><a href="#q23"> Can I use sventon together with Luntbuild?</a></li>
  <li><a href="#q24">Where can I find the change history?</a></li>
  <li><a href="#q25">Where can I find the sventon roadmap?</a></li>
  <li><a href="#q26">How can I use Glorbosoft XYZ to maximize team productivity?</a></li>
</ul>

<hr/>

<p>
<a name="q1">
<b>Q:</b> What is sventon?
</a>
<br/>
<b>A:</b> sventon is a pure java web application for browsing <a href="http://subversion.tigris.org">Subversion</a> version control system repositories.
</p>

<hr/>

<p>
<a name="q2">
<b>Q:</b> What features does sventon have?
</a>
<br/>
<b>A:</b> Read the <a href="index.php?page=features">feature</a> list.
</p>

<hr/>

<p>
<a name="q3">
<b>Q:</b> What do I need to run sventon?
</a>
<br/>
<b>A:</b> <a href="http://java.sun.com/j2se/1.5.0/">Java 1.5</a> or higher and a servlet container like <a href="http://jakarta.apache.org/tomcat/">Tomcat 5.5</a>. Note that Tomcat version 5.5.12 and newer includes a Java 5 compliant JSP compiler, which simplifies set-up further.
</p>

<hr/>

<p>
<a name="q4">
<b>Q:</b> What platforms are supported?
</a>
<br/>
<b>A:</b> Any platform capable of running a servlet container with <a href="http://java.sun.com/j2se/1.5.0/">Java 1.5</a> should do.
</p>

<hr/>

<p>
<a name="q5">
<b>Q:</b> What configurations have been tested?
</a>
<br/>
<b>A:</b> Test matrix:
</p>
<table style="white-space: nowrap; border-style: solid; border-width: 1px">
  <tr>
    <th>OS</th>
    <th>Subversion</th>
    <th>Container</th>
    <th>Protocols</th>
    <th>Java version</th>
    <th>Version</th>
  </tr>
  <tr>
    <td>WinXP</td>
    <td>1.4.2</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_04</td>
    <td>1.2.0</td>
  </tr>
  <tr>
    <td>Mac OS X 10.4.3</td>
    <td>1.2.1</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_05</td>
    <td>RC4</td>
  </tr>
</table>
<br/>

<hr/>

<p>
<a name="q6">
<b>Q:</b> How do I install and configure sventon?
</a>
<br/>
<b>A:</b> <a href="index.php?page=downloads">Download</a> the latest version and drop the <code>WAR</code> file in your servlet container's <code>webapps</code> directory. Point your web browser to <code>http://yourmachinename/svn/</code> and you should see the initial configuration page. If not, make sure your container is running (on the correct port). Submit the fields on the configuration page and you're done! The configuration will be stored in the generated file <code>WEB-INF/classes/sventon.properties</code> and will be used automatically next time the container starts.
</p>

<hr/>

<p>
<a name="q7">
<b>Q:</b> How do I uninstall sventon?
</a>
<br/>
<b>A:</b> Remove the file <code>svn.war</code> from the <code>webapps</code> directory and the subdirectory <code>svn</code>. The configuration and the cache files will be kept in a subdirectory called <code>sventon</code> in the container's <code>temp</code> directory. Find it and delete it.
</p>

<hr/>

<p>
<a name="q8">
<b>Q:</b> How does the cache/indexing work?
</a>
<br/>
<b>A:</b> The cache/index function, if configured to be active, enables features such as searching, directory flattening and log message search. The first time sventon is started all revisions will be cached aswell as the repository <tt>HEAD</tt>. This can take a couple of minutes depending on network speed and the number of entries. Each time a cache dependant action is triggered, the cache will be updated if necessary. There's also a scheduled job that triggers regularly to ensure that the index is up-to-date. The polling interval is default set to <code>10</code> minutes, but can be customized in <code>WEB-INF/sventon-servlet.xml</code>.<br/>A good reason for disabling the index is if the repository is really large and contains a lot of branches and tags, or if the network connection to it is just too slow.
</p>

<hr/>

<p>
<a name="q9">
<b>Q:</b> How do I upgrade sventon to a newer version?
</a>
<br/>
<b>A:</b> Simply replace the old <code>svn.war</code> file. The configuration files are stored in the container's <code>temp</code> directory, so they will be reused automatically.
</p>

<hr/>

<p>
<a name="q10">
<b>Q:</b> Will sventon in any way jeopardize my Subversion repository?
</a>
<br/>
<b>A:</b> No. Unless something really scary is going on, sventon will only perform <i>read</i> operations.
</p>

<hr/>

<p>
<a name="q11">
<b>Q:</b> Will sventon write any information to my Windows registry?
</a>
<br/>
<b>A:</b> No.
</p>

<hr/>

<p>
<a name="q12">
<b>Q:</b> Why is not file type [XYZ] colorized when displayed in sventon?
</a>
<br/>
<b>A:</b> sventon uses the <a href="https://jhighlight.dev.java.net">JHighlight</a> library to colorize files. Currently supported formats are:
</p>
<ul>
  <li>HTML&#47;XHTML</li>
  <li>Java</li>
  <li>Groovy</li>
  <li>C++</li>
  <li>XML</li>
  <li>LZX</li>
  <li><a href="http://rifers.org">RIFE</a></li>
</ul>
The file extension mapping is done in <code>WEB-INF/sventon-servlet.xml</code> to enable easy modification or additions.

<hr/>

<p>
<a name="q13">
<b>Q:</b> Why is the binary file type [XYZ] treated as a text file when I view it in sventon?
</a>
<br/>
<b>A:</b> Subversion's binary detection algorithm sometimes fails for binary files.
<br/>Since sventon 1.3 it is possible to override the detection by adding file extensions to the <code>textFileExtensionPattern</code> and the <code>binaryFileExtensionPattern</code> in <code>WEB-INF/sventon-servlet.xml</code> 
</p>

<hr/>

<p>
<a name="q14">
<b>Q:</b> Is there a log file for sventon?
</a>
<br/>
<b>A:</b> Yes, it's called <code>sventon.log</code> and is by default written to the container's <code>temp</code> directory. Logging can be customized by editing the file <code>WEB-INF/classes/log4j.properties</code>.
</p>

<hr/>

<p>
<a name="q15">
<b>Q:</b> Does sventon support non-US-ASCII charsets?
</a>
<br/>
<b>A:</b> Yes, hopefully. It is of course hard to test all possible combinations, so please report issues if you find any.
<br/>For Tomcat to work correctly with non-US-ASCII charsets, the Tomcat connector attribute <code>URIEncoding</code> should be set to <code>UTF-8</code>, or alternatively the attribute <code>useBodyEncodingForURI</code> should be set to <code>true</code>.
</p>

<hr/>

<p>
<a name="q16">
<b>Q:</b> Can I send suggestions for new functions?
</a>
<br/>
<b>A:</b> Yes, please do! But we cannot guarantee when or if your suggestion will be implemented.
</p>

<hr/>

<p>
<a name="q17">
<b>Q:</b> Will sventon be avaliable for CVS (or any other version control system) in the future?
</a>
<br/>
<b>A:</b> No, there are currently no such plans.
</p>

<hr/>

<p>
<a name="q18">
<b>Q:</b> What license do you use?
</a>
<br/>
<b>A:</b> The sventon code is licensed under the new BSD license. Read the full text <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/trunk/sventon/licenses/LICENSE">here</a>. Also, when you're at it, read <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/trunk/sventon/licenses/license-notes.txt">the license notes</a>. 
</p>

<hr/>

<p>
<a name="q19">
<b>Q:</b> Why did you start this project?
</a>
<br/>
<b>A:</b> Well, we kind of needed the functionality but we couldn't find what we were looking for, so we went ahead and started this project.
</p>

<hr/>

<p>
<a name="q20">
<b>Q:</b> Is there a publicly running version of sventon that I can try?
</a>
<br/>
<b>A:</b> Yes! You can browse the sventon Subversion repository using <a href="http://svn.sventon.org">sventon</a>.
<br/>
Please note that the repository and the sventon instance is hosted in two geographically different locations. This
could make the sventon instance response times sluggish from time to time.
</p>
<p>
Also, have a look at the
<a href="index.php?page=screenshots">screenshots</a> to get an idea of what a running sventon installation could look like.
</p>

<hr/>

<p>
<a name="q21">
<b>Q:</b> Can I change the layout of the RSS feed?
</a>
<br/>
<b>A:</b> Yes. It's template based, and the default template file is called <code>rsstemplate.html</code> and is located in <code>WEB-INF/classes</code>. Edit it the way you want it.
</p>

<hr/>

<p>
<a name="q22">
<b>Q:</b> Can I use sventon together with JIRA?
</a>
<br/>
<b>A:</b> Yes. An example configuration can be found <a href="http://confluence.atlassian.com/display/JIRAEXT/JIRA+Subversion+plugin#comment-36962434">here</a>.
</p>

<hr/>

<p>
<a name="q23">
<b>Q:</b> Can I use sventon together with Luntbuild?
</a>
<br/>
<b>A:</b> Yes. Starting with <a href="http://www.javaforge.com/proj/doc.do?doc_id=36361">Luntbuild version 1.5.2</a>
 support for sventon is included.
</p>

<hr/>

<p>
<a name="q24">
<b>Q:</b> Where can I find the change history?
</a>
<br/>
<b>A:</b> <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/branches/releases/1.3/sventon/doc/changes.txt">Here</a>!
</p>

<hr/>

<p>
<a name="q25">
<b>Q:</b> Where can I find the sventon roadmap?
</a>
<br/>
<b>A:</b> <a href="http://developer.berlios.de/feature/?group_id=3670">Here</a>!
</p>

<hr/>

<p>
<a name="q26">
<b>Q:</b> How can I use Glorbosoft XYZ to maximize team productivity?
</a>
<br/>
<b>A:</b> Many of our customers want to know how they can maximize productivity through our patented office groupware innovations. The answer is simple: first, click on the "File" menu, scroll down to "Increase Productivity", then...
<br/><br/>
Further reading: <a href="http://svnbook.red-bean.com/en/1.1/svn-book.html#svn-foreword">Version Control with Subversion</a>
</p>

<hr/>
</div>
