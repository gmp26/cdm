$(document).ready(function() {
	
var a0;
var a1;
var a2;
var a3;
var b0;
var b1;
var b2;
var b3;


// This function is called on page load and whenever you press restart
restart = function () {
	
	a0 =  randomRange(2,12);
	a1 =  randomRange(2,12);
	a2 =  randomRange(2,12);
	a3 =  randomRange(2,12);
	b0 =  randomRange(2,12);
	b1 =  randomRange(2,12);
	b2 =  randomRange(2,12);
	b3 =  randomRange(2,12);

};

// These functions determine whether to light a colour or not. Return true to light the colour
function yellowTest(n) {
	return rule[3](n);
}

function redTest(n) {
	return rule[2](n);
}

function blueTest(n) {
	return rule[1](n);
}

function greenTest(n) {
	return rule[0](n);
}



// Rule Table - pick the ones you want to use
var rule = [

	function(n) { return isLinear(n, a0, b0); },
	

	function(n) { return isLinear(n, a1, b1);  },
	

	function(n) { return isLinear(n, a2, b2);  },
	

	function(n) { return isLinear(n, a3, b3);  }
	
];


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
var flickering = false;

// Apply the tests to the number
testIt = function() {
	var number = $("#numberText").attr("value");
	var n = parseInt(number,10);
	flickering = false;
	
	if(!isNaN(n)) {
		lightup("#yellow", yellowTest(n));
		lightup("#red", redTest(n));
		lightup("#green", greenTest(n));
		lightup("#blue", blueTest(n));			
	}
	else {
		flickering = true;
		flicker("#yellow");
		flicker("#red");
		flicker("#green");
		flicker("#blue");
	}
};

function lightup(id, on) {
	$(id).stop();
	$(id).animate({opacity: (on ? 1 : 0)}, 250);
};

function flicker(id) {
	if(!flickering) return;
	$(id).animate({opacity: Math.random()*0.5}, 50*(1+Math.random()), "linear", function() {flicker(id); });
};

$("#numberText").keyup(testIt);
$("#restartButton").click(restart);

restart();
});

