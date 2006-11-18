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
  <li><a href="#q13">Is there a log file for sventon?</a></li>
  <li><a href="#q14">Does sventon support non-Latin 1 charsets?</a></li>
  <li><a href="#q15">Can I send suggestions for new functions?</a></li>
  <li><a href="#q16">Will sventon be avaliable for CVS (or any other version control system) in the future?</a></li>
  <li><a href="#q17">What license do you use?</a></li>
  <li><a href="#q18">Why did you start this project?</a></li>
  <li><a href="#q19">Is there a publicly running version of Sventon that I can try?</a></li>
  <li><a href="#q20">Can I change the layout of the RSS feed?</a></li>
  <li><a href="#q21">How can I use Glorbosoft XYZ to maximize team productivity?</a></li>
</ul>

<p>
<a name="q1">
<b>Q:</b> What is sventon?
</a>
<br/>
<b>A:</b> sventon is a pure java web based browser for the <a href="http://subversion.tigris.org">Subversion</a> version control system.
</p>

<p>
<a name="q2">
<b>Q:</b> What features does sventon have?
</a>
<br/>
<b>A:</b> Read the <a href="index.php?page=features">feature</a> list.
</p>

<p>
<a name="q3">
<b>Q:</b> What do I need to run sventon?
</a>
<br/>
<b>A:</b> <a href="http://java.sun.com/j2se/1.5.0/">Java 1.5</a> or higher and a servlet container like <a href="http://jakarta.apache.org/tomcat/">Tomcat 5.5</a>. Note that Tomcat version 5.5.12 and newer includes a Java 5 compliant JSP compiler, which simplifies set-up further.
</p>

<p>
<a name="q4">
<b>Q:</b> What platforms are supported?
</a>
<br/>
<b>A:</b> Any platform capable of running a servlet container with <a href="http://java.sun.com/j2se/1.5.0/">Java 1.5</a> should do.
</p>

<p>
<a name="q5">
<b>Q:</b> What configurations have been tested?
</a>
<br/>
<b>A:</b> Test matrix:
</p>
<table style="border-style: solid; border-width: 1px">
  <tr>
    <th>OS</th>
    <th>Subversion</th>
    <th>Container</th>
    <th>Protocols</th>
    <th>Java version</th>
    <th>Tester</th>
    <th>Version</th>
  </tr>
  <tr>
    <td>Win2k</td>
    <td>1.2.0 (RC 2)</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_04</td>
    <td>sventon committer</td>
    <td>RC3</td>
  </tr>
  <tr>
    <td>WinXP</td>
    <td>1.3.0</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_04</td>
    <td>sventon committer</td>
    <td>1.0.0</td>
  </tr>
  <tr>
    <td>Mac OS X 10.4.3</td>
    <td>1.2.1</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_05</td>
    <td>sventon committer</td>
    <td>RC4</td>
  </tr>
</table>

<p>
<a name="q6">
<b>Q:</b> How do I install and configure sventon?
</a>
<br/>
<b>A:</b> <a href="index.php?page=downloads">Download</a> the latest version and drop the <code>WAR</code> file in your servlet container's <code>webapps</code> directory. Point your web browser to <code>http://yourmachinename/svn/</code> and you should see the initial configuration page. If not, make sure your container is running (on the correct port). Submit the fields on the configuration page and you're done! The configuration will be stored in the generated file <code>WEB-INF/classes/sventon.properties</code> and will be used automatically next time the container starts.
</p>

<p>
<a name="q7">
<b>Q:</b> How do I uninstall sventon?
</a>
<br/>
<b>A:</b> Remove the <code>WAR</code> file from the <code>webapps</code> directory and the subdirectory <code>svn</code>. The index file, <code>sventon.idx</code>, will be kept in a directory specified during the initial configuration. Find it and delete it.
</p>

<p>
<a name="q8">
<b>Q:</b> How does the indexing work?
</a>
<br/>
<b>A:</b> The index function, if configured to be active, enables features such as searching and directory flattening. The first time sventon is started the repository <tt>HEAD</tt> will be indexed. This can take a couple of minutes depending on network speed and the number of entries. Each time the search or directory flattening functions are used, the index will be updated if necessary. There's also a scheduled job that triggers regularly and ensures that the index is up-to-date. The polling interval is default set to 10 minutes, but can be customized in <code>WEB-INF/sventon-servlet.xml</code>.<br/>A good reason for disabling the index is if the repository is really large and contains a lot of branches and tags, or if the network connection to it is slow.
</p>

<p>
<a name="q9">
<b>Q:</b> How do I upgrade sventon to a newer version?
</a>
<br/>
<b>A:</b> The easiest way to upgrade sventon is to replace the old <code>svn.war</code> file and walk through the configuration screen again. Another way is to make a backup copy of the configuration file <code>svn/WEB-INF/classes/sventon.properties</code>, replace the old <code>svn.war</code> with the new one to trigger a redeploy, and then add the backed up <code>sventon.properties</code> to the <code>svn/WEB-INF/classes</code> directory again.
</p>

<p>
<a name="q10">
<b>Q:</b> Will sventon in any way jeopardize my Subversion repository?
</a>
<br/>
<b>A:</b> No. Unless something really scary is going on, sventon will only perform <i>read</i> operations.
</p>

<p>
<a name="q11">
<b>Q:</b> Will sventon write any information to my Windows registry?
</a>
<br/>
<b>A:</b> No.
</p>

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

<p>
<a name="q13">
<b>Q:</b> Is there a log file for sventon?
</a>
<br/>
<b>A:</b> Yes, it's called <code>sventon.log</code> and is by default written to <code>java.io.tmpdir</code>. Logging can be customized by editing the file <code>WEB-INF/classes/log4j.properties</code>.
</p>

<p>
<a name="q14">
<b>Q:</b> Does sventon support non-Latin 1 charsets?
</a>
<br/>
<b>A:</b> Yes, hopefully. It is of course hard to test all possible combinations, so please report issues if you find any.
<br/>For Tomcat to work correctly with non-Latin 1 charsets, the Tomcat connector attribute <code>URIEncoding</code> should be set to <code>UTF-8</code>, or alternatively the attribute <code>useBodyEncodingForURI</code> should be set to <code>true</code>.
</p>

<p>
<a name="q15">
<b>Q:</b> Can I send suggestions for new functions?
</a>
<br/>
<b>A:</b> Yes, please do! But we cannot guarantee when or if your suggestion will be implemented.
</p>

<p>
<a name="q16">
<b>Q:</b> Will sventon be avaliable for CVS (or any other version control system) in the future?
</a>
<br/>
<b>A:</b> No.
</p>

<p>
<a name="q17">
<b>Q:</b> What license do you use?
</a>
<br/>
<b>A:</b> The sventon code is licensed under the new BSD license. Read the full text <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/trunk/sventon/licenses/LICENSE">here</a>. Also, when you're at it, read <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/trunk/sventon/licenses/license-notes.txt">the license notes</a>. 
</p>

<p>
<a name="q18">
<b>Q:</b> Why did you start this project?
</a>
<br/>
<b>A:</b> Well, we kind of needed the functionality but we couldn't find what we were looking for, so we went ahead and started this project.
</p>

<p>
<a name="q19">
<b>Q:</b> Is there a publicly running version of sventon that I can try?
</a>
<br/>
<b>A:</b> Although we unfortunately have no official demo version running, you should be able to find several installations doing a <a href="http://www.google.com/search?q=svn+sventon">Google search</a>. Note that new sventon versions are release regularily, the instances showing up in the search may not be running the latest version. Also, have a look at the <a href="index.php?page=screenshots">screenshots</a> to get an idea of what a running sventon installation could look like.
</p>

<p>
<a name="q20">
<b>Q:</b> Can I change the layout of the RSS feed?
</a>
<br/>
<b>A:</b> Yes. It's template based, and the default template file is called <code>rsstemplate.html</code> and is located in <code>WEB-INF/classes</code>. Edit it the way you want it.
</p>

<p>
<a name="q21">
<b>Q:</b> How can I use Glorbosoft XYZ to maximize team productivity?
</a>
<br/>
<b>A:</b> Many of our customers want to know how they can maximize productivity through our patented office groupware innovations. The answer is simple: first, click on the "File" menu, scroll down to "Increase Productivity", then...
<br/><br/>
Further reading: <a href="http://svnbook.red-bean.com/en/1.1/svn-book.html#svn-foreword">Version Control with Subversion</a>
</p>

