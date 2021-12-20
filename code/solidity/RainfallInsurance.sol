// SPDX-License-Identifier: MIT
pragma solidity >=0.4.16 <0.9.0;
contract RainfallInsurance {
    address public owner;
    WeatherData wd;
    mapping(address => uint) public policies;
    uint risk = 0;
    event Payout(address indexed holder, uint amount);
    event Balance(uint amount);
    event Rainfall(uint amount);

    function createPolicy(uint size) payable public {
        require(msg.value == size/10, "msg.value needs to be 1/10 of size");
        require(address(this).balance>=risk+size);
        policies[msg.sender] = size;
        risk = risk + size;
    }
    function closePolicy() public {
        require(policies[msg.sender]>0, "You do not have a policy");
        risk = risk - policies[msg.sender];
        policies[msg.sender] = 0;
    }
    function cancelPolicy(address user) public payable {
        require(msg.sender==owner);
        require(policies[user]>0, "User does not have a policy");
        payable(user).transfer(policies[user]/10);
        risk = risk - policies[user];
        emit Payout(user, policies[user]/10);
        policies[user] = 0;
    }
    function userClaim() public payable {
        require(policies[msg.sender]>0, "You do not have a policy");
        wd = WeatherData(0x2B717f348592895258741b02c72CCED7Acb8dd5D);
        require(wd.getRainfall()<50, "Rainfall was above 50 mm");
        payable(msg.sender).transfer(policies[msg.sender]);
        emit Payout(msg.sender, policies[msg.sender]);
        risk = risk - policies[msg.sender];
        policies[msg.sender] = 0;
    }
    function claimForUser(address _user) public payable {
        require(policies[_user]>0, "User does not have a policy");
        wd = WeatherData(0x2B717f348592895258741b02c72CCED7Acb8dd5D);
        require(wd.getRainfall()<50, "Rainfall was above 50 mm");
        payable(_user).transfer(policies[_user]);
        emit Payout(_user, policies[_user]);
        risk = risk - policies[_user];
        policies[_user] = 0;
    }
    function withdrawProfits(uint amount) payable public{
        require(msg.sender == owner);
        require(amount <= address(this).balance-risk, "Not enough Profits");
        payable(owner).transfer(amount);

    }
    function deposit() payable public {
        require(msg.sender == owner);
    }
    function balance() public {
        emit Balance(address(this).balance);
    }
    function rainfall() public {
        wd = WeatherData(0x2B717f348592895258741b02c72CCED7Acb8dd5D);
        uint rain = wd.getRainfall();
        emit Rainfall(rain);
    }
    receive() external payable{ }
    fallback() external payable{ }
    constructor ()  {
       owner = msg.sender;
    }
}

contract WeatherData {
    function setRainfall(uint x) public {}
    function getRainfall() external view returns(uint) {}
    constructor () {}
    }
