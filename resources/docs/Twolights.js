// JavaScript Document


	var a0;
	var a1;
	var b0;
	var b1;
	
	

	// This function is called on page load and whenever you press restart
	function restart() {
		
		a0 =  randomRange(2,12);
		a1 =  randomRange(2,12);
		b0 =  randomRange(2,12);
		b1 =  randomRange(2,12);
		

	}
	
	// These functions determine whether to light a colour or not. Return true to light the colour

	function redTest(n) {
		return rule[0](n);
	}
	
	function blueTest(n) {
		return rule[1](n);
	}
	


	// Rule Table - pick the ones you want to use
	var rule = [

		function(n) { return isLinear(n, a0, b0) },
		

		function(n) { return isLinear(n, a1, b1)  },
		
	
		]
	
	
	// Various number tests
	function isWholeNumber(n) {
		return Math.abs(Math.round(n) - n) < epsilon;
	}
	
	function isMultipleOf(n, a) {
		return isWholeNumber(n/a);
	}
	
	function isLinear(n, a, b) {
		return isWholeNumber((n-b)/a);
	}
	
	function isInList(n, list) {
		for(var i=0; i < list.length; i++) {
			if(Math.abs(list[i]-n) < epsilon) return true;
		}
		return false;
	}

	function isPrime(n) {
		if(primes[n]) return true;
		for(var p = 3; p <= Math.sqrt(n); p+=2) {
			if(n%p == 0)
				return false;
		}
		return primes[n]=true;
	}
	
	function isSquare(n) {
		if(n < 0) return false;
		return isWholeNumber(Math.sqrt(n));
	}
	
	function isTriangular(n) {
		return isSquare(8*n+1);
	}
	
	// Pick a random whole number in [a,b]
	function randomRange(a,b) {
		return Math.floor(a + (b-a)*Math.random());
	}
	
	// return the digits of the number as an Array
	function digits(n) {
		ar = [];
		if(!isWholeNumber(n)) return ar;
		return pushDigits(ar,n);
	}
	function pushDigits(ar, n) {
		m = Math.round(n/10);
		d = n - 10*m;
		ar.push(d);
		if(m > 0)
			return pushDigits(ar, m);
		return ar;
	}
	
	// return the sumDigits
	function sumDigits(n) {
		var ar = digits(n);
		var sum = 0;
		for(var i=0; i < ar.length; i++) {
			sum += digits[i];
		}
		return sum;
	}
	
	// keep summing digits till we have a digit
	function digitSum(n) {
		if(!isWholeNumber(n)) return -1;
		for(s = n; s >= 10; s = sumDigits(n)) {}
		return s;
	}
	
	
	var epsilon = 1e-13;
	var primes = {2:true};


	// Apply the tests to the number
	function testIt() {
		var number = $("#numberText").attr("value");
		var n = parseInt(number);
		
		if(!isNaN(n)) {
			lightup("#red", redTest(n));
			lightup("#blue", blueTest(n));			
		}
		else {
			flicker("#red");
			flicker("#blue");
		}
	}
	
	function lightup(id, on) {
		$(id).stop();
		$(id).animate({opacity: (on ? 1 : 0)}, 250)
	}
	
	function flicker(id) {
		$(id).animate({opacity: Math.random()*0.5}, 50*(1+Math.random()), "linear", function() {flicker(id)});
	}
	
	function init() {
		//$("#numberText").change(testIt);
		$("#numberText").keyup(testIt);
	}


