var colorst = '#34a263';
var BaseCompCharts = function () {

    var $flotStacked = jQuery('.js-flot-stacked');

    // Demo Data
    var $dataEarnings = [[1, 2500], [2, 2300], [3, 3200], [4, 2500], [5, 4500], [6, 2800], [7, 3900], [8, 3100], [9, 4600], [10, 3200], [11, 4200], [12, 5700]];
    var $dataSales = [[1, 1100], [2, 700], [3, 1300], [4, 900], [5, 1900], [6, 950], [7, 1700], [8, 1250], [9, 1800], [10, 1300], [11, 1750], [12, 2900]];

    var $dataAspir = [[1, 1500], [2, 500], [3, 1900], [4, 100], [5, 1100], [6, 550], [7, 1300], [8, 1050], [9, 2000], [10, 1500], [11, 1550], [12, 3400]];


    var $dataMonths = [[1, 'Ene'], [2, 'Feb'], [3, 'Mar'], [4, 'Abr'], [5, 'May'], [6, 'Jun'], [7, 'Jul'], [8, 'Ago'], [9, 'Sep'], [10, 'Oct'], [11, 'Nov'], [12, 'Dic']];

    var initChartsFlot = function () {

        // Stacked Chart
        jQuery.plot($flotStacked,
                [
                    {
                        label: 'Egresados',
                        data: $dataSales
                    },
                    {
                        label: 'Aspirantes',
                        data: $dataAspir
                    },
                    {
                        label: 'Ingresados',
                        data: $dataEarnings
                    }
                ],
                {
                    colors: ['#faad7d', '#fadb7d', '#7cf97c'],
                    series: {
                        stack: true,
                        lines: {
                            show: true,
                            fill: true
                        }
                    },
                    lines: {show: true,
                        lineWidth: 0,
                        fill: true,
                        fillColor: {
                            colors: [{opacity: 1}, {opacity: 1}]
                        }
                    },
                    legend: {
                        show: true,
                        position: 'nw',
                        sorted: true,
                        backgroundOpacity: 0
                    },
                    grid: {
                        borderWidth: 0
                    },
                    yaxis: {
                        tickColor: '#ffffff',
                        ticks: 3
                    },
                    xaxis: {
                        ticks: $dataMonths,
                        tickColor: '#f5f5f5'
                    }
                }
        );

        // Get the elements where we will attach the charts

        var $flotLive = jQuery('.js-flot-live');

        // Live Chart
        var $dataLive = [];

        function getRandomData() { // Random data generator

            if ($dataLive.length > 0)
                $dataLive = $dataLive.slice(1);

            while ($dataLive.length <= 50) {
                var UsersNumber;
                //Pruebas
                $.ajax({
                    url: '../Transporte/queryCsessionCounter.jsp',
                    data: '&txtBandera=1',
                    type: 'post',
                    success: function (resp) {
                        UsersNumber = resp;
                        jQuery('#bandera').val(UsersNumber);
                    }
                });


                var users = $('#bandera').val();

//                var prev = $dataLive.length > 0 ? $dataLive[$dataLive.length - 1] : 50;
//                var y = prev + Math.random() * 10 - 5;
//                if (y < 0)
//                    y = 0;
//                if (y > 100)
//                    y = 100;
//                

//                var prev = $dataLive.length > 0 ? $dataLive[$dataLive.length - 1] : 50; 
                var y = users * 1;

                if (y < 0)
                    y = 0;
                if (y > 100)
                    y = 100;


                if (y > 0 && y < 33) {
                    jQuery('#colors').val('#34a263');
                } else if (y > 33 && y < 66) {
                    jQuery('#colors').val('#efa231');

                } else if (y > 66) {
                    jQuery('#colors').val('#c54736');
                }

                $dataLive.push(y);
            }
            colorst = jQuery('#colors').val();
            //alert(colorst);
            if (colorst == '') {

                colorst = '#34a263';
            }
            var res = [];
            for (var i = 0; i < $dataLive.length; ++i)
                res.push([i, $dataLive[i]]);

            // Show live chart info
            jQuery('.js-flot-live-info').html(y.toFixed(0) + '%');
            jQuery('#bandera1').val(res);
            //alert(res);
            return res;
        }

        function updateChartLive() { // Update live chart
            if (colorst == '#34a263') {
                $chartLivegreen.setData([getRandomData()]);
                $chartLivegreen.draw();

            } else if (colorst == '#efa231') {
                $chartLiveyellow.setData([getRandomData()]);
                $chartLiveyellow.draw();

            } else if (colorst == '#c54736') {
                $chartLiveRed.setData([getRandomData()]);
                $chartLiveRed.draw();

            }

            setTimeout(updateChartLive, 1000);
        }



        var $chartLivegreen = jQuery.plot($flotLive, // Init live chart
                [{data: getRandomData()}],
                {
                    series: {
                        shadowSize: 0
                    },
                    lines: {
                        show: true,
                        lineWidth: 2,
                        fill: true,
                        fillColor: {
                            colors: [{opacity: .2}, {opacity: .2}]
                        }
                    },
                    colors: ['#34a263'],
                    grid: {
                        borderWidth: 0,
                        color: '#aaaaaa'
                    },
                    yaxis: {
                        show: true,
                        min: 0,
                        max: 100
                    },
                    xaxis: {
                        show: false
                    }
                }
        );
        var $chartLiveyellow = jQuery.plot($flotLive, // Init live chart
                [{data: getRandomData()}],
                {
                    series: {
                        shadowSize: 0
                    },
                    lines: {
                        show: true,
                        lineWidth: 2,
                        fill: true,
                        fillColor: {
                            colors: [{opacity: .2}, {opacity: .2}]
                        }
                    },
                    colors: ['#efa231'],
                    grid: {
                        borderWidth: 0,
                        color: '#aaaaaa'
                    },
                    yaxis: {
                        show: true,
                        min: 0,
                        max: 100
                    },
                    xaxis: {
                        show: false
                    }
                }
        );

        var $chartLiveRed = jQuery.plot($flotLive, // Init live chart
                [{data: getRandomData()}],
                {
                    series: {
                        shadowSize: 0
                    },
                    lines: {
                        show: true,
                        lineWidth: 2,
                        fill: true,
                        fillColor: {
                            colors: [{opacity: .2}, {opacity: .2}]
                        }
                    },
                    colors: ['#c54736'],
                    grid: {
                        borderWidth: 0,
                        color: '#aaaaaa'
                    },
                    yaxis: {
                        show: true,
                        min: 0,
                        max: 100
                    },
                    xaxis: {
                        show: false
                    }
                }
        );

        updateChartLive(); // Start getting new data

    };
    return {
        init: function () {
            initChartsFlot();
        }
    };
}();

jQuery(function () {
    BaseCompCharts.init();
});


