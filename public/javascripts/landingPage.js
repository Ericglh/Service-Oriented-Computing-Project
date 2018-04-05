var app = angular.module('CMSApp', ['ngPrint']);

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

app.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function (file, uploadUrl) {
        var fd = new FormData();
        fd.append('file', file);

        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': 'multipart/form-data', 'enctype': 'multipart/form-data'}
        }).success(function () {
            })

            .error(function () {
            });
    }
}]);


app.controller('landingPageCtrl', ['$scope', '$http', '$location', '$sce', '$rootScope', function ($scope, $http, $location, $sce, $rootScope) {


    $scope.conferenceID = "";
    $scope.myConferences = "";
    $scope.curConference = "Choose your conf";
    $scope.curPaperId;
    $scope.showAssignedTable = false;
    $scope.baseUrl = $location.href;
    $scope.port = $location.port();
    $rootScope.adminconf = "";
    $rootScope.pcconf = "";
    $scope.userList = [];
    $rootScope.flag = true;
    $rootScope.submitUser = "";

    $scope.setFlag = function (f) {
        $rootScope.flag = f;
    }

    $scope.getFlag = function () {
        return $rootScope.flag;
    }

    $scope.setSubmitUser = function (u) {
        $rootScope.submitUser = u;
    }

    $scope.getSubmitUser = function () {
        return $rootScope.submitUser;
    }

    $scope.setAdminConf = function (u) {
        $rootScope.adminconf = u;
    }

    $scope.getAdminConf = function () {
        return $rootScope.adminconf;
    }

    $scope.setPCChairConf = function (p) {
        $rootScope.pcconf = p;
    }

    $scope.getPCChairConf = function () {
        return $rootScope.pcconf;
    }



    $scope.showMainPageBool = true;
    $scope.showMyAccountPageBool = false;
    $scope.showMyConferencePageBool = false;
    $scope.showsubmitPaperPageBool = false;
    $scope.showChangeProfilePage = false;
    $scope.showUpdatePaperPageBool = false;
    $scope.showReviewPaperPageBool = false;
    $scope.showAdminPageBool = false;
    $scope.showPCChairPageBool = false;
    $scope.showAssignedTable = false;


    $scope.showMyAccountPage = function () {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = true;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.updateReviewFormBool = false;
        $scope.showAssignedTable = false;


        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/users/' + curUsername).then(function successCallback(response) {
                //console.log(response);
                $scope.showMyProfileArr = response.data;
            }, function errorCallback(response) {
                $scope.message = "Show user profile failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }


    $scope.showMyConferencePage = function () {

        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = true;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.updateReviewFormBool = false;
        $scope.showAssignedTable = false;

        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/confs/users/' + curUsername + '/all').then(function successCallback(response) {
                //console.log("sss" + response);
                $scope.allConf = response.data;
                // for(var i = 0;i < $scope.allConf.length;i++){
                //     if($scope.allConf[i].reviewAccess == 'yes')
                //         $scope.allConf[i].reviewAccess
                // }
            }, function errorCallback(response) {
                $scope.message = "Get confs failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }


    $scope.showMainPage = function () {

        $scope.showMainPageBool = true;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.updateReviewFormBool = false;
        $scope.showAssignedTable = false;

        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/confs/users/' + curUsername + '/submission').then(function successCallback(response) {
                //console.log(response);
                $scope.myConferences = response.data;
                $scope.myConferences.length == 0 ? $scope.curConference = '' : $scope.curConference = $scope.myConferences[0];
                $http.get('/papers/confs/' + $scope.curConference.id + '/users/' + curUsername).then(function successCallback(response) {
                    //console.log(response);
                    $scope.myPapers = response.data;
                }, function errorCallback(response) {
                    $scope.message = "Get confs failed";
                    console.log($scope.message);
                });
            }, function errorCallback(response) {
                $scope.message = "Get confs failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }

    $scope.showMainPage();


    $scope.submitPaper = function (conf) {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = true;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.showAssignedTable = false;
        $scope.curConference = conf;
        //alert("flag is: " + $scope.getFlag());
        if($scope.getFlag() == true){
            $scope.pEmail = $scope.getSubmitUser();
        }

    }

    $scope.changeProfile = function () {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = true;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.showAssignedTable = false;

        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/users/' + curUsername).then(function successCallback(response) {
                $scope.defaultProfile = response.data;
                $scope.uEmail = $scope.defaultProfile.email;
                $scope.uTitle = $scope.defaultProfile.title;
                $scope.uRAreas = $scope.defaultProfile.researchAreas;
                $scope.uFName = $scope.defaultProfile.firstName;
                $scope.uLName = $scope.defaultProfile.lastName;
                $scope.uPosition = $scope.defaultProfile.position;
                $scope.uAffliation = $scope.defaultProfile.affiliation;
                $scope.uPhone = $scope.defaultProfile.phone;
                $scope.uFax = $scope.defaultProfile.fax;
                $scope.uAddress = $scope.defaultProfile.address;
                $scope.uCity = $scope.defaultProfile.city;
                $scope.uCRegion = $scope.defaultProfile.country;
                $scope.uZipcode = $scope.defaultProfile.zipCode;
                $scope.uComments = $scope.defaultProfile.comments;
                console.log($scope.defaultProfile);
            }, function errorCallback(response) {
                $scope.message = "Get papers failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }
    $scope.updatePaper = function (paperId) {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = true;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.showAssignedTable = false;
        $scope.curPaperId = paperId;

        $http.get('/papers/' + paperId).then(function successCallback(response) {
            $scope.defaultPaper = response.data;
            $scope.uTitle = $scope.defaultPaper.title;
            $scope.uTopic = $scope.defaultPaper.topic;
            $scope.uEmail = $scope.defaultPaper.email;
            $scope.uOUser = $scope.defaultPaper.otherAuthors;
            $scope.uAbstract = $scope.defaultPaper.abstractText;
            console.log($scope.defaultPaper);
        }, function errorCallback(response) {
            $scope.message = "Get papers failed";
            console.log($scope.message);
        });


    }

    $scope.showReviewPaperPage = function (confid) {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = true;
        $scope.showAdminPageBool = false;
        $scope.showPCChairPageBool = false;
        $scope.showAssignedTable = false;

        // /confs/reviewers/{username}
        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/confs/reviewers/' + curUsername).then(function successCallback(response) {
                console.log('get reviews ' + response.data);
                $scope.myReviews = response.data;
            }, function errorCallback(response) {
                $scope.message = "Get papers failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.showAllAdminPage = function (confid) {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = true;
        $scope.updateReviewFormBool = false;
        $scope.showAssignedTable = false;
        $scope.showPCChairPageBool = true;
        //confid that will be used in the whole admin page.
        //alert("In the showAdminPage func " + confid);
        //$rootScope.adminConf = confid;
        $scope.setAdminConf(confid);
        $scope.downloadSub = $scope.getAdminConf();
        $scope.downloadAcc = $scope.getAdminConf();
    }


    $scope.showAdminPage = function (confid) {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = true;
        $scope.updateReviewFormBool = false;
        $scope.showAssignedTable = false;
        $scope.showPCChairPageBool = false;
        //confid that will be used in the whole admin page.
        //alert("In the showAdminPage func " + confid);
        //$rootScope.adminConf = confid;
        $scope.setAdminConf(confid);
        $scope.downloadSub = $scope.getAdminConf();
        $scope.downloadAcc = $scope.getAdminConf();
    }

    $scope.showPCChairPage = function (confid) {
        $scope.showMainPageBool = false;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.showAdminPageBool = false;
        $scope.updateReviewFormBool = false;
        $scope.showAssignedTable = false;
        $scope.showPCChairPageBool = true;
        //confid that will be used in the whole admin page.
        //alert("In the showAdminPage func " + confid);
        //$rootScope.adminConf = confid;
        $scope.setAdminConf(confid);
        $scope.setPCChairConf(confid);
        $scope.downloadSub = $scope.getAdminConf();
        $scope.downloadAcc = $scope.getAdminConf();
    }


    $scope.logout = function () {
        // $scope.showMainPageBool = true;
        // $scope.showMyAccountPageBool = false;
        // $scope.showMyConferencePageBool = false;
        // $scope.showsubmitPaperPageBool = false;
        // $scope.showChangeProfilePage = false;
        // $scope.showUpdatePaperPageBool = false;
        $http.post('/users/logout').then(function successCallback(response) {
            //console.log(response);
            window.location.href = "/";
        }, function errorCallback(response) {
            $scope.message = "Get papers failed";
            console.log($scope.message);
        });
    }


    $scope.submitPaper1 = function () {

        //alert($scope.pTitle + $scope.pEmail + $scope.pCEmail + $scope.pOUser + $scope.pAbstract + $scope.SV + $scope.bestP);
        if($scope.getAdminConf() !== undefined && $scope.getAdminConf().length !== 0){
            $http.put('/papers', {
                "confId": $scope.getAdminConf(),
                "title": $scope.pTitle,
                "topic": $scope.pTopic,
                "email": $scope.pEmail,
                "awardCandidate": $scope.bestP,
                "studentVolunteer": $scope.SV,
                "abstractText": $scope.pAbstract,
                "authors": $scope.choices,
                "otherAuthors": $scope.pOUser
            }).then(function successCallback(response) {
                //console.log(response);
                // alert("Thank you. Your paper abstract has been submitted successfully. Please keep your paper id: XXX.");
                $scope.setAdminConf("");
                $scope.showMainPage();
            }, function errorCallback(response) {
                $scope.message = "Login failed, please check your user name and password.";
            });
        }else{
            $http.put('/papers', {
                "confId": $scope.curConference.id,
                "title": $scope.pTitle,
                "topic": $scope.pTopic,
                "email": $scope.pEmail,
                "awardCandidate": $scope.bestP,
                "studentVolunteer": $scope.SV,
                "abstractText": $scope.pAbstract,
                "authors": $scope.choices,
                "otherAuthors": $scope.pOUser
            }).then(function successCallback(response) {
                //console.log(response);
                // alert("Thank you. Your paper abstract has been submitted successfully. Please keep your paper id: XXX.");
                $scope.setAdminConf("");
                $scope.showMainPage();
            }, function errorCallback(response) {
                $scope.message = "Login failed, please check your user name and password.";
            });
        }



    }

    $scope.updatePaperForm = function () {
        $http.post('/papers', {
            "paperId": $scope.curPaperId,
            "title": $scope.uTitle,
            "topic": $scope.uTopic,
            "email": $scope.uEmail,
            "awardCandidate": $scope.ubestP,
            "studentVolunteer": $scope.uSV,
            "abstractText": $scope.uAbstract,
            "authors": $scope.choices,
            "otherAuthors": $scope.uOUser
        }).then(function successCallback(response) {
            //console.log(response);
            alert("Thank you. Your paper abstract has been submitted successfully. Please keep your paper id: XXX.");
            $scope.showMainPage();
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
        $scope.showMainPage();


    }

    $scope.uploadFile = function (paperId, myfile) {


        console.log(myfile);

        $http.post('/papers/upload/' + paperId, {
            "doc": myfile,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then(function successCallback(response) {
            //console.log(response);
            alert("Thank you. Your paper has been submitted successfully.");
            $scope.showMainPage();
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }


    $scope.chooseConf = function (myConference) {
        $scope.curConference = myConference;
        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/papers/confs/' + $scope.curConference.id + '/users/' + curUsername).then(function successCallback(response) {
                //console.log(response);
                $scope.myPapers = response.data;
                console.log($scope.myPapers);
            }, function errorCallback(response) {
                $scope.message = "Get papers failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }


    $scope.choices = [{id: 'user1'}];

    $scope.addNewChoice = function () {

        if ($scope.choices.length >= 7) {
            alert('Max 7 User');
        } else {
            var newItemNo = $scope.choices.length + 1;
            $scope.choices.push({'id': 'user' + newItemNo});
        }

    };

    $scope.removeChoice = function () {
        var lastItem = $scope.choices.length - 1;
        $scope.choices.splice(lastItem);
    };

    $scope.updateUProfile = function () {

        $scope.showMainPage();
        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.post('/users/profile', {
                "username": curUsername,
                "title": $scope.uTitle,
                "researchAreas": $scope.uRAreas,
                "firstName": $scope.uFName,
                "lastName": $scope.uLName,
                "position": $scope.uPosition,
                "affiliation": $scope.uAffliation,
                "email": $scope.uEmail,
                "phone": $scope.uPhone,
                "fax": $scope.uFax,
                "address": $scope.uAddress,
                "city": $scope.uCity,
                "country": $scope.uCRegion,
                "zipCode": $scope.uZipcode,
                "comments": $scope.uComments
            }).then(function successCallback(response) {
                //console.log(response);
                $scope.showMainPage();
            }, function errorCallback(response) {
                $scope.message = "Update user profile failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.toMainPage = function (conf) {
        $scope.showMainPageBool = true;
        $scope.showMyAccountPageBool = false;
        $scope.showMyConferencePageBool = false;
        $scope.showsubmitPaperPageBool = false;
        $scope.showChangeProfilePage = false;
        $scope.showUpdatePaperPageBool = false;
        $scope.showReviewPaperPageBool = false;
        $scope.updateReviewFormBool = false;

        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $scope.curConference = conf;
            $http.get('/papers/confs/' + $scope.curConference.id + '/users/' + curUsername).then(function successCallback(response) {
                //console.log(response);
                $scope.myPapers = response.data;
            }, function errorCallback(response) {
                $scope.message = "Get confs failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.reviewAccess = function (review, confid) {
        if (review == 'yes') {
            $scope.showReviewPaperPage(confid);
        } else {

        }
    }

    $scope.adminAccess = function (admin, chair, confid) {
        if (admin == 'yes' && chair == 'yes') {
            // $scope.showAdminPage(confid);
            // $scope.showPCChairPage(confid);
            $scope.showAllAdminPage(confid);
        } else if(admin == 'yes'){
            $scope.showAdminPage(confid);
        } else if(chair == 'yes'){
            $scope.showPCChairPage(confid);
        } else {
            //$scope.showAdminPage(confid);
            alert("No Access");
        }
    }

    $scope.showAssignedPage = function (conf) {
        $scope.updateReviewFormBool = false;
        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $scope.curConference = conf;

            $http.get('/papers/confs/' + $scope.curConference.id + '/reviewers/' + curUsername).then(function successCallback(response) {
                $scope.showAssignedTable = true;
                $scope.myAssigns = response.data;
            }, function errorCallback(response) {
                $scope.message = "Get confs failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.showUpdateReview = function (paperid, num) {

        var curPaperId = paperid;
        $scope.curPaperId = curPaperId;
        $scope.updateReviewFormBool = true;
        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.get('/reviews/papers/' + $scope.curPaperId + '/reviewers/' + curUsername).then(function successCallback(response) {
                $scope.myCurReview = response.data.content;

                console.log(response);
                if (num == 1) {
                    $scope.readOnly = false;
                } else {
                    $scope.readOnly = true;
                }
            }, function errorCallback(response) {
                if (num == 1) {
                    $scope.readOnly = false;
                } else {
                    $scope.readOnly = true;
                }
                $scope.myCurReview = '';
                $scope.message = "Get confs failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.submitReview = function () {
        $http.get('/currentUsername').then(function successCallback(response) {
            var curUsername = response.data.username;
            $http.post('/reviews', {
                "paperId": $scope.curPaperId,
                "username": curUsername,
                "content": $scope.myCurReview
            }).then(function successCallback(response) {
                $scope.updateReviewFormBool = false;
                $scope.showReviewPaperPage($scope.curConference.id);
            }, function errorCallback(response) {
                $scope.message = "Update user profile failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.exitReview = function () {
        $scope.updateReviewFormBool = false;
    }

    $scope.downloadPaper = function (paperid) {
        $http.get('/papers/download/' + paperid).then(function successCallback(response) {
            alert(response);
        }, function errorCallback(response) {
            alert("No paper available");
            console.log($scope.message);
        });
    }


    //adminconf -> id
    //admin Page
    $scope.setPCChairPageFalse = function(){
        $scope.showPCReviewManageBool = false;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;
    }

    $scope.setAdminPageFalse = function(){

        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
    }


    $scope.showConfigurePage = function () {
        $scope.configurePage = true;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();

        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.confName = response.data.acronym;
            $scope.confDate = response.data.date;
            $scope.confLocation = response.data.location;
            $scope.confSubmissionDeadline = response.data.submissionDeadline;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }

    $scope.changeConfigure = function () {
        $http.post('/confs/configure', {
            "confId": $scope.getAdminConf(),
            "acronym": $scope.confName,
            "date": $scope.confDate,
            "submissionDeadline": $scope.confSubmissionDeadline,
            "location": $scope.confLocation
        }).then(function successCallback(response) {
            alert("Change configuration successfully!");
            console.log("Post successfully");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.showProgramCommittee = function () {
        $scope.configurePage = false;
        $scope.programCommittee = true;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();
    }
    
    $scope.assignReviewer = function () {
        $http.post('/confs/assignReviewer', {
            "confId": $scope.getAdminConf(),
            "username": $scope.assignReview
        }).then(function successCallback(response) {
            alert("Change configuration successfully!");
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Assign error");
        });
    }

    $scope.showResearchTopic = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = true;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();

        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.researchTopics = response.data.researchTopics;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }
    $scope.researchTopicText = "";
    $scope.addResearchTopic = function () {
        var topic = $scope.researchTopicText;
        $http.put('/confs/researchTopics', {
            "confId": $scope.getAdminConf(),
            "researchTopic": {
                "label" : topic
            }
        }).then(function successCallback(response) {
            $scope.researchTopicText = "";
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.researchTopics = response.data.researchTopics;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Assign error");
        });
    }

    $scope.deleteTopic = function (topicId) {
        //alert("topicId: " + topicId + " confId: " + $scope.getAdminConf());
        $http.delete("/confs/" + $scope.getAdminConf() + "/researchTopics/" + topicId).then(function successCallback(response) {
            alert("Delete a topic successfully!");
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.researchTopics = response.data.researchTopics;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Delete error");
        });
    }

    $scope.showCriteria = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = true;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();

        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.criterias = response.data.criteria;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });


    }
    $scope.criteriaLabel = "";
    $scope.criteriaExp = "";
    $scope.criteriaWeight = "";
    $scope.deleteCriteria = function (id) {
        ///confs/{confId}/criteria/{criterionId}
        $http.delete("/confs/" + $scope.getAdminConf() + "/criteria/" + id).then(function successCallback(response) {
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.criterias = response.data.criteria;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Delete error");
        });
    }

    $scope.addCriteria = function () {
        $http.put('/confs/criteria', {
            "confId": $scope.getAdminConf(),
            "criterion": {
                "label" : $scope.criteriaLabel,
                "explanations" : $scope.criteriaExp,
                "weight" : $scope.criteriaWeight
            }
        }).then(function successCallback(response) {
            $scope.criteriaLabel = "";
            $scope.criteriaExp = "";
            $scope.criteriaWeight = "";
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.criterias = response.data.criteria;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Assign error");
        });

        //You should invoke http.get to refresh the criteria div
    }

    $scope.reviewQ = "";
    $scope.showReviewQuestions = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = true;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();

        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.reviewQuestions = response.data.reviewQuestions;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });


    }

    $scope.deleteReviewQuestion = function (id) {
        ///confs/{confId}/reviewQuestions/{reviewQuestionId}
        $http.delete("/confs/" + $scope.getAdminConf() + "/reviewQuestions/" + id).then(function successCallback(response) {
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.reviewQuestions = response.data.reviewQuestions;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Delete error");
        });
    }

    $scope.addReviewQuestions = function () {
        $http.put('/confs/reviewQuestions', {
            "confId": $scope.getAdminConf(),
            "reviewQuestion": {
                "question" : $scope.reviewQ
            }
        }).then(function successCallback(response) {
            $scope.reviewQ = "";
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.reviewQuestions = response.data.reviewQuestions;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Assign error");
        });

        //You should invoke http.get to refresh the criteria div
    }



    $scope.showStatusCode = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = true;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();

        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.statusCodes1 = response.data.statusCodes;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }
    $scope.scLabel = "";
    $scope.scMailTemplateLabel = "";
    $scope.deleteStatusCode = function (id) {
        ///confs/{confId}/statusCodes/{statusCodeId}
        $http.delete("/confs/" + $scope.getAdminConf() + "/statusCodes/" + id).then(function successCallback(response) {
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.statusCodes1 = response.data.statusCodes;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Delete error");
        });
    }

    $scope.addStatusCode = function () {
        //alert("cameraRequired" + $scope.cameraRequired);
        $http.put('/confs/statusCodes', {
            "confId": $scope.getAdminConf(),
            "statusCode": {
                "label" : $scope.scLabel,
                "mailTemplate" : $scope.scMailTemplateLabel,
                "cameraReadyRequired" : $scope.cameraRequired
            }
        }).then(function successCallback(response) {
            $scope.scLabel = "";
            $scope.scMailTemplateLabel = "";
            $scope.cameraRequired = false;
            $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
                $scope.statusCodes1 = response.data.statusCodes;
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Assign error");
        });

    }




    $scope.showSubmissionPhase = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = true;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();
    }

    $scope.changePhase = function () {
        $http.post('/confs/status', {
            "confId": $scope.getAdminConf(),
            "status": $scope.phase
        }).then(function successCallback(response) {
            alert("Change phase successfully!");
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Assign error");
        });
    }


    $scope.showEmailPage = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = true;
        $scope.submitPapers = false;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();

    }

    $scope.viewSubmissionTemplate = function () {
        $scope.showViewSub = true;
        $scope.showViewRem = false;
        $scope.showModifySub = false;
        $scope.showModifyRem = false;

        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            console.log(response.data.submissionEmailTemplate);
            $scope.submissionTemplate = response.data.submissionEmailTemplate;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.modifySubmissionTemplate = function () {
        $scope.showViewSub = false;
        $scope.showViewRem = false;
        $scope.showModifySub = true;
        $scope.showModifyRem = false;
    }

    $scope.submitSub = function (submissionTemplate) {
        $http.post('/confs/submissionEmailTemplate', {
            "confId": $scope.getAdminConf(),
            "template": submissionTemplate
        }).then(function successCallback(response) {
            console.log("Post successfully");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }
    $scope.submitRem = function (reminderTemplate) {
        $http.post('/confs/reminderEmailTemplate', {
            "confId": $scope.getAdminConf(),
            "template": reminderTemplate
        }).then(function successCallback(response) {
            console.log("Post successfully");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.viewReminderTemplate = function () {
        $scope.showViewSub = false;
        $scope.showViewRem = true;
        $scope.showModifySub = false;
        $scope.showModifyRem = false;

        $scope.readRemOnly = true;
        $http.get('/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            console.log(response.data.reminderEmailTemplate);
            $scope.reminderTemplate = response.data.reminderEmailTemplate;
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.modifyReminderTemplate = function () {
        $scope.showViewSub = false;
        $scope.showViewRem = false;
        $scope.showModifySub = false;
        $scope.showModifyRem = true;

        $scope.readRemOnly = false;
    }
    $scope.showSubmitPapers = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = true;
        $scope.downloadPapers = false;
        $scope.setPCChairPageFalse();
    }

    $scope.submitPaperOn = function () {
        //alert("test: " + $scope.submitUsername);
        $scope.setFlag(true);
        $scope.setSubmitUser($scope.submitUsername1);
        $scope.submitPaper($scope.getAdminConf());
        //$scope.submitUsername = $scope.submitUsername1;
    }
    
    
    $scope.showDownloadPapers = function () {
        $scope.configurePage = false;
        $scope.programCommittee = false;
        $scope.researchTopic = false;
        $scope.criteria = false;
        $scope.reviewQuestions = false;
        $scope.statusCode = false;
        $scope.submissionPhase = false;
        $scope.emailPage = false;
        $scope.submitPapers = false;
        $scope.downloadPapers = true;
        $scope.setPCChairPageFalse();

        $http.get('/papers/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.adminAllPapers = response.data;
            console.log($scope.adminAllPapers);
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    //Submission phase



    $scope.showPCReviewM = function () {
        $scope.showPCReviewManageBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

        $http.get('/users/reviewers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
            $scope.listOfReviewers = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });



    }

    $scope.deleteAReviewer = function (name) {
        $http.delete("/confs/" + $scope.getPCChairConf() + "/removeReviewer/" + name).then(function successCallback(response) {
            alert("Delete a topic successfully!");
            $http.get('/users/reviewers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfReviewers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Delete error");
        });
    }

    $scope.showListOfSP = function () {
        $scope.showPCReviewManageBool = false;
        $scope.PCSubmitPaperBool = true;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;


        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

        $http.get('/papers/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.listOfSP = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });




    }

    $scope.listOfReviewStates = "";

    $scope.assignUsername = "";
    $scope.assignPaperID = "";
    $scope.removePaperID = "";
    $scope.removeUsername = "";
    $scope.showManualAssign = function () {
        $scope.showPCReviewManageBool = false;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = true;
        $scope.showListOfPapersBool = false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

        $http.get('/papers/confs/' + $scope.getAdminConf()).then(function successCallback(response) {
            $scope.listOfManual = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }

    $scope.manualAssign = function () {
        $http.post('/papers/assignReviewer', {
            "username": $scope.assignUsername,
            "paperId": $scope.assignPaperID
        }).then(function successCallback(response) {
            $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfManual = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.manualRemove = function () {
        $http.post('/papers/removeReviewer', {
            "username": $scope.removeUsername,
            "paperId": $scope.removePaperID
        }).then(function successCallback(response) {
            $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfManual = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.showReviewState = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showReviewStateBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();


        $http.get('/users/reviewers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {

            for(var i = 0;i < response.data.length;i++){
                var len1 = response.data[i].reviewedPapers.length;
                var len2 = response.data[i].notReviewedPapersJa.length;
                var reviewed = "";
                var notreviewed = "";
                for(var j = 0;j < len1;j++){
                    reviewed += response.data[i].reviewedPapers[j];
                }
                for(var k = 0;k < len2;k++){
                    notreviewed += response.data[i].notReviewedPapersJa[k];
                }
                response.data[i].reviewedPapers = reviewed;
                response.data[i].notReviewedPapersJa = notreviewed;
            }

            $scope.listOfReviewStates = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });


    }

    $scope.showSendReminders = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showSendRemindersBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

    }

    $scope.sendReminderToAllReviewers = function () {
        $http.post('/confs/reviewerReminder', {
            "confId": $scope.getPCChairConf()
        }).then(function successCallback(response) {
            console.log("Post successfully");
            alert("Send reminders successfully!");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }


    $scope.pendingReviewer = "";
    $scope.showPendingM = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showPendingMBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

        $http.get('/confs/' + $scope.getPCChairConf() + '/pendingReviewers').then(function successCallback(response) {
            $scope.pendingReviewers = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }
    $scope.pendingReviewer = "";
    $scope.addPendingReviewer = function () {
        $http.post('/confs/assignPendingReviewer', {
            "confId": $scope.getPCChairConf(),
            "username": $scope.pendingReviewer
        }).then(function successCallback(response) {
            console.log("Post successfully");
            alert("Add successfully!");
            $http.get('/confs/' + $scope.getPCChairConf() + '/pendingReviewers').then(function successCallback(response) {
                $scope.pendingReviewers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });

    }
    
    $scope.deletePendingReviewer = function (name) {
        $http.delete("/confs/" + $scope.getPCChairConf() + "/pendingReviewer/" + name).then(function successCallback(response) {
            alert("Delete a topic successfully!");
            $http.get('/confs/' + $scope.getPCChairConf() + '/pendingReviewers').then(function successCallback(response) {
                $scope.pendingReviewers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
            console.log("Post successfully");
        }, function errorCallback(response) {
            alert("Delete error");
        });

    }

    $scope.showSearchPaperById = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showSearchPaperByIdBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();
    }

    $scope.searchPaperId = "";
    $scope.searchPById = function () {
        //$scope.searchPaperLabel
        //seearchP

        $http.get('/papers/' + $scope.searchPaperId).then(function successCallback(response) {
            $scope.searchP = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });

    }

    //Select phase

    $scope.filterStatus = "";
    $scope.showPCStatusOfPaper = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showStatusOfPaperBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();
    }

    $scope.showAccepted = function () {
        $scope.filterStatus = "Accept";
    }

    $scope.showRejected = function () {
        $scope.filterStatus = "Reject";
    }

    $scope.showMoved = function () {
        $scope.filterStatus = "Moved";
    }

    $scope.showTransferred = function () {
        $scope.filterStatus = "Transferred";
    }

    $scope.showAll = function () {
        $scope.filterStatus = "";
    }

    $scope.ListOfAuthors = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = true;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

        $http.get('/users/authors/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
            $scope.listOfAuthors = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });


    }

    $scope.showSendRemindersNotSubmit = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showSendRemindersNotSubmitBool = true;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool =false;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();
    }

    $scope.remind = function () {
        $http.post('/confs/reviewerWithUnreviewedReminder', {
            "confId": $scope.getPCChairConf()
        }).then(function successCallback(response) {
            console.log("Post successfully");

        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }



    $scope.showListOfPapers = function () {
        $scope.showPCReviewManageBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool = true;
        $scope.showListOfAuthorsBool = false;
        $scope.showPCMailsBool = false;


        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();
    }
    
    $scope.showMailsPage = function () {
        $scope.showPCMailsBool = true;
        $scope.showPCReviewManageBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showStatusOfPaperBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool = false;
        $scope.showListOfAuthorsBool = false;


        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();
    }

    $scope.listOfPapers = "";
    $scope.showStatusOfPapers = function () {
        $scope.showStatusOfPaperBool = true;
        $scope.showPCMailsBool = false;
        $scope.showPCReviewManageBool = false;
        $scope.showSendRemindersNotSubmitBool = false;
        $scope.PCSubmitPaperBool = false;
        $scope.showReviewStateBool = false;
        $scope.showSendRemindersBool = false;
        $scope.showPendingMBool = false;
        $scope.showSearchPaperByIdBool = false;
        $scope.showManualAssignBool = false;
        $scope.showListOfPapersBool = false;
        $scope.showListOfAuthorsBool = false;

        $scope.configurePage = false;
        $scope.emailPage = false;
        $scope.programCommittee = false;
        $scope.setAdminPageFalse();

        $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
            $scope.listOfPapers = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }

    $scope.modifyAccepted = function (id) {
        $http.post('/papers/status', {
            "paperId": id,
            "status": "Accept"
        }).then(function successCallback(response) {
            $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfPapers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.modifyRejected = function (id) {
        $http.post('/papers/status', {
            "paperId": id,
            "status": "Reject"
        }).then(function successCallback(response) {
            $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfPapers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.modifyMoved = function (id) {
        $http.post('/papers/status', {
            "paperId": id,
            "status": "Moved"
        }).then(function successCallback(response) {
            $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfPapers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.modifyTransferred = function (id) {
        $http.post('/papers/status', {
            "paperId": id,
            "status": "Transferred"
        }).then(function successCallback(response) {
            $http.get('/papers/confs/' + $scope.getPCChairConf()).then(function successCallback(response) {
                $scope.listOfPapers = response.data;
                console.log(response.data);
            }, function errorCallback(response) {
                $scope.message = "Get current user failed";
                console.log($scope.message);
            });
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }


    $scope.showDetailReviewsBool = false;
    $scope.listOfReviews = "";
    $scope.showReviewsDetail = function (paperid) {
        ///reviews/papers/{paperId}
        $scope.showDetailReviewsBool = true;
        $http.get('/reviews/papers/' + paperid).then(function successCallback(response) {
            $scope.listOfReviews = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            $scope.message = "Get current user failed";
            console.log($scope.message);
        });
    }
    

    $scope.emailTitle1 = "";
    $scope.emailContent = "";
    $scope.mailReviewers = function () {
        $http.post('/confs/reviewerReminder', {
            "confId": $scope.getPCChairConf()
        }).then(function successCallback(response) {
            console.log("Post successfully");
            alert("Send reminders successfully!");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

    $scope.mailAuthors = function () {
        $http.post('/confs/emailAuthors', {
            "confId": $scope.getPCChairConf(),
            "title": $scope.emailTitle1,
            "content": $scope.emailContent
        }).then(function successCallback(response) {
            console.log("Post successfully");
            alert("Send reminders successfully!");
        }, function errorCallback(response) {
            $scope.message = "Login failed, please check your user name and password.";
        });
    }

}]);
