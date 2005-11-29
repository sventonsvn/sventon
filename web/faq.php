<ul>
  <li><a href="#q1">What is sventon?</a></li>
  <li><a href="#q2">What features does sventon have?</a></li>
  <li><a href="#q3">What do I need to run sventon?</a></li>
  <li><a href="#q4">What platforms are supported?</a></li>
  <li><a href="#q5">What configurations have been tested?</a></li>
  <li><a href="#q6">How do I install and configure sventon?</a></li>
  <li><a href="#q7">I have followed the installation instructions and restarted the web server but nothing happens when I point my browser to <code>http://yourmachinename/svn/</code>. Why?</a></li>
  <li><a href="#q8">How do I uninstall sventon?</a></li>
  <li><a href="#q9">Will sventon in any way jeopardize my Subversion repository?</a></li>
  <li><a href="#q10">Will sventon write any information to my Windows registry?</a></li>
  <li><a href="#q11">On which servlet containers has sventon been tested?</a></li>
  <li><a href="#q12">Why is not file type [XYZ] colorized when displayed in sventon?</a></li>
  <li><a href="#q13">Can I send suggestions for new functions?</a></li>
  <li><a href="#q14">Will sventon be avaliable for CVS (or any other version control system) in the future?</a></li>
  <li><a href="#q15">What license do you use?</a></li>
  <li><a href="#q16">Why did you start this project?</a></li>
  <li><a href="#q17">Is there a publicly running version of Sventon that I can try?</a></li>
  <li><a href="#q18">How can I use Glorbosoft XYZ to maximize team productivity?</a></li>
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
<b>A:</b> <a href="http://java.sun.com/j2se/1.5.0/">Java 1.5</a> or higher and a servlet container like <a href="http://jakarta.apache.org/tomcat/">Tomcat 5.5</a>.
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
<table style="border-style: solid; border-width: 1px">
  <tr>
    <th>OS</th>
    <th>Subversion</th>
    <th>Container</th>
    <th>Protocols</th>
    <th>Java version</th>
    <th>Tester</th>
    <th>Revision</th>
  </tr>
  <tr>
    <td>Win2k</td>
    <td>1.2.0 (RC 2)</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_04</td>
    <td>sventon committer</td>
    <td>249</td>
  </tr>
  <tr>
    <td>Mac OS X 10.4.3</td>
    <td>1.2.1</td>
    <td>Tomcat 5.5.9</td>
    <td>svn/dav</td>
    <td>1.5.0_05</td>
    <td>sventon committer</td>
    <td>249</td>
  </tr>
</table>
</p>

<p>
<a name="q6">
<b>Q:</b> How do I install and configure sventon?
</a>
<br/>
<b>A:</b> <a href="index.php?page=downloads">Download</a> the latest version and drop the <code>WAR</code> file in your servlet container's <code>webapps</code> directory. Point your web browser to <code>http://yourmachinename/svn/</code> and you should see the initial configuration page. If not, make sure your container is running (on the correct port). Submit the fields on the configuration page and restart your servlet container when you are ask to do so. Point your web browser again to <code>http://yourmachinename/svn/</code> and you're done!
</p>

<p>
<a name="q7">
<b>Q:</b> I have followed the installation instructions and restarted the web server but nothing happens when I point my browser to <code>http://yourmachinename/svn/</code>. Why?
</a>
<br/>
<b>A:</b> First time sventon is started the index will be built up. This can take a couple of seconds or even minutes, all depending on network speed and the number of repository entries. Just be patient...
</p>

<p>
<a name="q8">
<b>Q:</b> How do I uninstall sventon?
</a>
<br/>
<b>A:</b> Remove the <code>WAR</code> file from the <code>webapps</code> directory and the subdirectory <code>svn</code>. The index file, <code>sventon.idx</code>, will be kept in a directory specified during the initial configuration. Find it and delete it.
</p>

<p>
<a name="q9">
<b>Q:</b> Will sventon in any way jeopardize my Subversion repository?
</a>
<br/>
<b>A:</b> No. Unless something really scary is going on, sventon will only perform <i>read</i> operations.
</p>

<p>
<a name="q10">
<b>Q:</b> Will sventon write any information to my Windows registry?
</a>
<br/>
<b>A:</b> No.
</p>

<p>
<a name="q11">
<b>Q:</b> On which servlet containers have sventon been tested?
</a>
<br/>
<b>A:</b> Currently only on Tomcat 5.5.
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
  <li>XML</li>
  <li>LZX</li>
  <li><a href="https://rife.dev.java.net">RIFE</a></li>
</ul>

<p>
<a name="q13">
<b>Q:</b> Can I send suggestions for new functions?
</a>
<br/>
<b>A:</b> Yes, please do! But we cannot guarantee when or if your suggestion will be implemented
</p>

<p>
<a name="q14">
<b>Q:</b> Will sventon be avaliable for CVS (or any other version control system) in the future?
</a>
<br/>
<b>A:</b> No.
</p>

<p>
<a name="q15">
<b>Q:</b> What license do you use?
</a>
<br/>
<b>A:</b> The sventon code is licensed under the new BSD license. Read the full text <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/trunk/licenses/LICENSE">here</a>. Also, when you're at it, read <a href="http://svn.berlios.de/viewcvs/*checkout*/sventon/trunk/licenses/license-notes.txt">the license notes</a>. 
</p>

<p>
<a name="q16">
<b>Q:</b> Why did you start this project?
</a>
<br/>
<b>A:</b> Well, we kind of needed the functionality but we couldn't find what we were looking for, so we went ahead and started this project.
</p>

<p>
<a name="q17">
<b>Q:</b> Is there a publicly running version of sventon that I can try?
</a>
<br/>
<b>A:</b> Unfortunately no, not that we know of. Please have a look at the <a href="index.php?page=screenshots">screenshots</a> to get an idea of what a running sventon installation could look like.
</p>

<p>
<a name="q18">
<b>Q:</b> How can I use Glorbosoft XYZ to maximize team productivity?
</a>
<br/>
<b>A:</b> Many of our customers want to know how they can maximize productivity through our patented office groupware innovations. The answer is simple: first, click on the "File" menu, scroll down to "Increase Productivity", then...
<br/><br/>
Well, no, not really. But this is such a great FAQ question we couldn't resist stealing it from: <a href="http://svnbook.red-bean.com/en/1.1/svn-book.html#svn-foreword">Version Control with Subversion</a>
</p>

