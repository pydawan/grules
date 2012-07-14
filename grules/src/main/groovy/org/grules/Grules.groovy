package org.grules

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Cookie

import org.grules.script.RulesScriptGroupResult
import org.grules.script.RulesScriptResult


/**
 * API class for interacting with grules functionality.
 */
class Grules {
		
	/**
	 * Runs a rules script against grouped parameters. The parameters are defined as a map which keys are group names and
	 * values are maps of (paramater name -> parameter value) pairs. Usage example:
	 * <code><br><br>
	 * RulesScriptGroupResult result = Grules.applyGroupRules(TestScriptRules, GET: [id: 1], POST: [login: "admin"])<br>
	 * </code>
	 * @param rulesScript rules script class
	 * @param parameters input parameters
	 * @return result of rules application
	 */
	static RulesScriptGroupResult applyGroupRules(Map<String, Map<String, Object>> parameters, 
		  Class<? extends Script> rulesScript) {
		Closure<RulesScriptGroupResult> preprocessor = newGroupRulesApplicator(rulesScript)
		preprocessor(parameters)
	}
	
  /** See {@link #applyGroupRules(Map, Class)*/		
	static RulesScriptGroupResult applyGroupRules(Class<? extends Script> rulesScript, 
			Map<String, Map<String, Object>> parameters) {
		applyGroupRules(parameters, rulesScript)
	}

	/**
   * Runs a rules script against non-grouped parameters. The parameters are defined as a map of (paramater name -> 
   * parameter value) pairs. Usage example:
	 * <code><br><br>
	 * RulesScriptResult result = Grules.applyRules(TestScriptRules, id: 1, login: "admin")<br>
	 * </code>
	 * @param rulesScript rules script class
	 * @param parameters input parameters
	 * @return result of rules application
	 */
	static RulesScriptResult applyRules(Map<String, Object> parameters, Class<? extends Script> rulesScript) {
		Closure<RulesScriptResult> preprocessor = newRulesApplicator(rulesScript)
		preprocessor(parameters)
	}
	
	/** See {@link #applyRules(Map, Class) */
	static RulesScriptResult applyRules(Class<? extends Script> rulesScript, Map<String, Object> parameters) {
		applyRules(parameters, rulesScript)
	}

	/**
	 * Creates a closure that runs a given rules script against passed parameters (grouped). Usage example:
	 * <code><br><br>
	 * def test = Grules.newGroupRulesApplicator(TestScriptRules)<br>
	 * RulesScriptGroupResult result1 = test(GET: [id: 1, style: "default"], POST: [login: "admin"])
	 * RulesScriptGroupResult result2 = test(GET: [id: 2])
	 * </code>
	 * @param rulesScript rules script class
	 * @return closure that runs the script
	 */
  static Closure<RulesScriptGroupResult> newGroupRulesApplicator(Class<? extends Script> rulesScript) {
	  GrulesInjector.ruleEngine.newGroupExecutor(rulesScript)
  }

  /**
	 * Creates a closure that runs a given rules script against passed parameters (ungrouped). Usage example:
	 * <code><br><br>
	 * def test = Gruless.newRulesApplicator(AllInOneRules)<br>
	 * RulesScriptResult result1 = test(id: 1, style: "default")
	 * RulesScriptResult result2 = test(id: 2)
	 *
	 * @param rulesScript rules script class
	 * @return closure that runs the script
	 */
  static Closure<RulesScriptResult> newRulesApplicator(Class<? extends Script> rulesScript) {
	  GrulesInjector.ruleEngine.newExecutor(rulesScript)
  }

	/**
	 * Creates a map with request HTTP headers from the given request object.
	 * 
	 * @param request HTTP request object
	 * @return map with headers 
	 */
	static Map<String, Map<String, Object>> fetchRequestHeaders(HttpServletRequest request) {
		Enumeration<String> headerNames = request.headerNames
		headerNames.toList().collectEntries {	String name ->
			[(name): request.getHeader(name)]
		}
	}
			
 /**
	* Creates a map with HTTP parameters from the given request object.
	*
	* @param request HTTP request object
	* @param listParameters collection of parameters that represent list of values not a single string
	* @return map with parameters, where key is a parameter name and value is a string or a list of strings
	*/
	static Map<String, Map<String, Object>> fetchRequestParameters(HttpServletRequest request,
		  List<String> listParameters = []) {
  	request.parameterMap.collectEntries { String name, String[] values ->
			 [(name): name in listParameters ? values as List<String> : values[0]]
		}
	}
				 
 /**
  * Creates a map with cookies from the given request object.
	*
	* @param request HTTP request object
  * @return map with cookies
	*/
	static Map<String, Map<String, String>> fetchRequestCookies(HttpServletRequest request) {
		(request.cookies as List<Cookie>).collectEntries { Cookie cookie ->
				[(cookie.name): cookie.value]
		}
	}
}