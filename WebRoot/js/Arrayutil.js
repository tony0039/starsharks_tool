function getArrBykey(arr, attr, value) {
  if (!arr || arr.length == 0) {
    return ""
  }
  var newArr = arr.filter(function (item, index) {
    return item[attr] == value
  })
  return newArr
}
function removekey(arr, attr, value) {
  if (!arr || arr.length == 0) {
    return ""
  }
  var newArr = arr.filter(function (item, index) {
    return item[attr] != value
  })
  return newArr
}
function updateKey(arr,attr,before,after){
	if (!arr || arr.length == 0) {
	    return ""
	  }
	  var newArr = arr.filter(function (item, index) {
		  if(item[attr]==before){
			  return item[attr]=after;
		  }
	    return item[attr] != value
	  })
	temp=removekey(arr,attr,before);
	temp.put(newArr);
	 
}
/**测试删除元素
var list=[{
	"name":"aaa",
	"age":18
},{
	"name":"bbb",
	"age":28
}]
list=removekey(list,"name","aaa");
console.log(list);
**/