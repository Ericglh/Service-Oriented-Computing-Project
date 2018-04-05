var app = angular.module('CMSApp', ['angular-md5']);

app.controller('homePageController', ['$scope', '$http', '$location', 'md5', function ($scope, $http, $location, md5) {


    $scope.regOrLogin = true;
    $scope.regPage = false;
    $scope.loginPage = false;
    $scope.showsQ = false;

    $scope.showLoginPage = function () {
        $scope.regOrLogin = false;
        $scope.regPage = false;
        $scope.loginPage = true;
    }

    $scope.showRegPage = function () {

        $scope.regOrLogin = false;
        $scope.regPage = true;
        $scope.loginPage = false;
    }

    $scope.back = function () {

        $scope.regOrLogin = true;
        $scope.regPage = false;
        $scope.loginPage = false;
    }


    $scope.regUser = function () {
        //alert($scope.userid + $scope.password + $scope.backupEmail + $scope.email + $scope.userTitle + $scope.researchAreas + $scope.firstName + $scope.lastName + $scope.position + $scope.affliation + $scope.phone + $scope.fax + $scope.address + $scope.city + $scope.countryRegion + $scope.zipcode + $scope.comments);
        //alert(md5.createHash($scope.password));
        //alert($scope.register_username);
        $http.put('/users', {
            "username": $scope.register_username,
            "password": md5.createHash($scope.register_password),
            "backupEmail": $scope.backupEmail,
            "securityQuestion": $scope.securityQuestion,
            "answer": $scope.answer,
            "title": $scope.userTitle,
            "researchAreas": $scope.researchAreas,
            "firstName": $scope.firstName,
            "lastName": $scope.lastName,
            "position": $scope.position,
            "affiliation": $scope.affliation,
            "email": $scope.email,
            "phone": $scope.phone,
            "fax": $scope.fax,
            "address": $scope.address,
            "city": $scope.city,
            "country": $scope.countryRegion,
            "zipCode": $scope.zipcode,
            "comments": $scope.comments
        }).then(function successCallback(response) {
            //console.log(response);
            console.log("Register new user successfully!");

            window.location.href = "/landingPage";

        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }
    $scope.username = '';
    $scope.password = '';
    $scope.validation = {
        username: function () {
            var userlen = $scope.username.length;
            var savekeywords = $scope.username.match(/^about$|^access$|^account$|^accounts$|^add$|^address$|^adm$|^admin$|^administration$|^adult$|^advertising$|^affiliate$|^affiliates$|^ajax$|^analytics$|^android$|^anon$|^anonymous$|^api$|^app$|^apps$|^archive$|^atom$|^auth$|^authentication$|^avatar$|^backup$|^banner$|^banners$|^bin$|^billing$|^blog$|^blogs$|^board$|^bot$|^bots$|^business$|^chat$|^cache$|^cadastro$|^calendar$|^campaign$|^careers$|^cgi$|^client$|^cliente$|^code$|^comercial$|^compare$|^config$|^connect$|^contact$|^contest$|^create$|^code$|^compras$|^css$|^dashboard$|^data$|^db$|^design$|^delete$|^demo$|^design$|^designer$|^dev$|^devel$|^dir$|^directory$|^doc$|^docs$|^domain$|^download$|^downloads$|^edit$|^editor$|^email$|^ecommerce$|^forum$|^forums$|^faq$|^favorite$|^feed$|^feedback$|^flog$|^follow$|^file$|^files$|^free$|^ftp$|^gadget$|^gadgets$|^games$|^guest$|^group$|^groups$|^help$|^home$|^homepage$|^host$|^hosting$|^hostname$|^html$|^http$|^httpd$|^https$|^hpg$|^info$|^information$|^image$|^img$|^images$|^imap$|^index$|^invite$|^intranet$|^indice$|^ipad$|^iphone$|^irc$|^java$|^javascript$|^job$|^jobs$|^js$|^knowledgebase$|^log$|^login$|^logs$|^logout$|^list$|^lists$|^mail$|^mail1$|^mail2$|^mail3$|^mail4$|^mail5$|^mailer$|^mailing$|^mx$|^manager$|^marketing$|^master$|^me$|^media$|^message$|^microblog$|^microblogs$|^mine$|^mp3$|^msg$|^msn$|^mysql$|^messenger$|^mob$|^mobile$|^movie$|^movies$|^music$|^musicas$|^my$|^name$|^named$|^net$|^network$|^new$|^news$|^newsletter$|^nick$|^nickname$|^notes$|^noticias$|^ns$|^ns1$|^ns2$|^ns3$|^ns4$|^old$|^online$|^operator$|^order$|^orders$|^page$|^pager$|^pages$|^panel$|^password$|^perl$|^pic$|^pics$|^photo$|^photos$|^photoalbum$|^php$|^plugin$|^plugins$|^pop$|^pop3$|^post$|^postmaster$|^postfix$|^posts$|^profile$|^project$|^projects$|^promo$|^pub$|^public$|^python$|^random$|^register$|^registration$|^root$|^ruby$|^rss$|^sale$|^sales$|^sample$|^samples$|^script$|^scripts$|^secure$|^send$|^service$|^shop$|^sql$|^signup$|^signin$|^search$|^security$|^settings$|^setting$|^setup$|^site$|^sites$|^sitemap$|^smtp$|^soporte$|^ssh$|^stage$|^staging$|^start$|^subscribe$|^subdomain$|^suporte$|^support$|^stat$|^static$|^stats$|^status$|^store$|^stores$|^system$|^tablet$|^tablets$|^tech$|^telnet$|^test$|^test1$|^test2$|^test3$|^teste$|^tests$|^theme$|^themes$|^tmp$|^todo$|^task$|^tasks$|^tools$|^tv$|^talk$|^update$|^upload$|^url$|^user$|^username$|^usuario$|^usage$|^vendas$|^video$|^videos$|^visitor$|^win$|^ww$|^www$|^www1$|^www2$|^www3$|^www4$|^www5$|^www6$|^www7$|^wwww$|^wws$|^wwws$|^web$|^webmail$|^website$|^websites$|^webmaster$|^workshop$|^xxx$|^xpg$|^you$|^yourname$|^yourusername$|^yoursite$|^yourdomain$/);
            $scope.userdisable = false;
            if (userlen < 3 || savekeywords) {
                $scope.userdisable = true;
                if (userlen < 3) {
                    return "Username should be at least three characters";
                }
                if (savekeywords) {
                    return "Username invalid, please choose another name";
                }
            } else {
                return "Username valid";
            }
        },
        password: function () {
            var passwordlen = $scope.password.length;
            $scope.passworddisable = false;
            if (passwordlen < 6) {
                $scope.passworddisable = true;
                return "Password should be at least six characters";
            } else {
                return "Password valid";
            }
        }
    };

    $scope.login_or_signup = function () {
        //console.log("hello");
        var username = $scope.username;
        var password = md5.createHash($scope.password);
        // alert(username + password);

        $http.post('/users/login', {
            "username": username,
            "password": password
        }).then(function successCallback(response) {
            console.log("Login successfully!");

            window.location.href = "/landingPage";

        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
            console.log($scope.message);
        });
    }

    $scope.resetPassword = function (username) {
        $http.get('/users/' + $scope.username).then(function successCallback(response) {
            $scope.showsQ = true;
            $scope.sQuestion = response.data.securityQuestion;
        }, function errorCallback(response) {

        });
    }

    $scope.submitQuestion = function () {
        $http.post('/users/resetPassword', {
            "username": $scope.username,
            "answer": $scope.sAnswer
        }).then(function successCallback(response) {
            console.log("reset successfully!");
            window.location.href = "";
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
            console.log($scope.message);
        });
    }

}]);
