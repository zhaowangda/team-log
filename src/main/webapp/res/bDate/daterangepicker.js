/**
 * @version: 1.0
 * @author: Dan Grossman http://www.dangrossman.info/
 * @date: 2012-08-20
 * @copyright: Copyright (c) 2012 Dan Grossman. All rights reserved.
 * @license: Licensed under Apache License v2.0. See http://www.apache.org/licenses/LICENSE-2.0
 * @website: http://www.improvely.com/
 */
!function($) {

  var DateRangePicker = function(element, options, cb) {

    //state
    this.startDate = Date.today();
    this.endDate = Date.today();
    this.ranges = {};
    this.opens = 'right';
    this.cb = function() { };
    this.format = 'yyyy-MM-dd';

    this.leftCalendar = {
      month: Date.today().set({day: 1, month: this.startDate.getMonth(), year: this.startDate.getFullYear()}),
      calendar: Array()
    };

    this.rightCalendar = {
      month: Date.today().set({day: 1, month: this.endDate.getMonth(), year: this.endDate.getFullYear()}),
      calendar: Array()
    }

    //element that triggered the date range picker
    this.element = $(element);

    if (this.element.hasClass('pull-right'))
      this.opens = 'left';

    if (this.element.is('input')) {
      this.element.on({
        click: $.proxy(this.show, this),
        focus: $.proxy(this.show, this),
        blur: $.proxy(this.hide, this)
      });
    } else {
      this.element.on('click', $.proxy(this.show, this));
    }

    //the date range picker
    this.container = $(DRPTemplate).appendTo('body');

    if (typeof options == 'object') {
      if (typeof options.ranges == 'object') {
        for (var range in options.ranges) {

          var start = options.ranges[range][0];
          var end = options.ranges[range][1];

          if (typeof start == 'string')
            start = Date.parse(start);
          if (typeof end == 'string')
            end = Date.parse(end);

          this.ranges[range] = [start, end];
        }

        var list = '<ul>';
        for (var range in this.ranges) {
          list += '<li>' + range + '</li>';
        }
        list += '<li>'+this.customLabel+'</li>';
        list += '</ul>';
        this.container.find('.ranges').prepend(list);

      }

      if (typeof options.format == 'string')
        this.format = options.format

      if (typeof options.opens == 'string')
        this.opens = options.opens;

    }

    if (this.opens == 'right') {
      //swap calendar positions
      var left = this.container.find('.calendar.left');
      var right = this.container.find('.calendar.right');
      left.removeClass('left').addClass('right');
      right.removeClass('right').addClass('left');
    }

    if (typeof options == 'undefined' || typeof options.ranges == 'undefined')
      this.container.find('.calendar').show();

    if (typeof cb == 'function')
      this.cb = cb;

    this.container.addClass('opens' + this.opens);

    //event listeners
    this.container.on('mousedown', $.proxy(this.mousedown, this));
    this.container.find('.calendar').on('click', '.prev', $.proxy(this.clickPrev, this));
    this.container.find('.calendar').on('click', '.next', $.proxy(this.clickNext, this));
    this.container.find('.ranges').on('click', 'button', $.proxy(this.clickApply, this));

    this.container.find('.calendar').on('click', 'td', $.proxy(this.clickDate, this));
    this.container.find('.calendar').on('mouseenter', 'td', $.proxy(this.enterDate, this));
    this.container.find('.calendar').on('mouseleave', 'td', $.proxy(this.updateView, this));

    this.container.find('.ranges').on('click', 'li', $.proxy(this.clickRange, this));
    this.container.find('.ranges').on('mouseenter', 'li', $.proxy(this.enterRange, this));
    this.container.find('.ranges').on('mouseleave', 'li', $.proxy(this.updateView, this));

    this.updateView();
    this.updateCalendars();

  };

  DateRangePicker.prototype = {

    constructor: DateRangePicker,
    customLabel:"自定义日期段",

    mousedown: function(e) {
      e.stopPropagation();
      e.preventDefault();
    },

    updateView: function() {
      this.leftCalendar.month.set({month: this.startDate.getMonth(), year: this.startDate.getFullYear()});
      this.rightCalendar.month.set({month: this.endDate.getMonth(), year: this.endDate.getFullYear()});

      this.container.find('input[name=daterangepicker_start]').val(this.startDate.toString(this.format));
      this.container.find('input[name=daterangepicker_end]').val(this.endDate.toString(this.format));

      if (this.startDate.equals(this.endDate) || this.startDate.isBefore(this.endDate)) {
        this.container.find('button').removeAttr('disabled');
      } else {
        this.container.find('button').attr('disabled', 'disabled');
      }
    },

    notify: function() {
      this.updateView();

      if (this.element.is('input')) {
        this.element.val(this.startDate.toString(this.format) + ' - ' + this.endDate.toString(this.format));
      }
      this.cb(this.startDate, this.endDate);
    },

    move: function() {
      if (this.opens == 'left') {
        this.container.css({
          top: this.element.offset().top + this.element.outerHeight(),
          right: $(window).width() - this.element.offset().left - this.element.outerWidth(),
          left: 'auto'
        });
      } else {
        this.container.css({
          top: this.element.offset().top + this.element.outerHeight(),
          left: this.element.offset().left,
          right: 'auto'
        });
      }
    },

    show: function(e) {
      this.container.show();
      this.move();

      if (e) {
        e.stopPropagation();
        e.preventDefault();
      }

         $(document).on('mousedown', $.proxy(this.hide, this));
    },

    hide: function(e) {
      this.container.hide();
      $(document).off('mousedown', this.hide);
    },

    enterRange: function(e) {
      var label = e.target.innerHTML;
      if (label == this.customLabel) {
        this.updateView();
      } else {
        var dates = this.ranges[label];
        this.container.find('input[name=daterangepicker_start]').val(dates[0].toString(this.format));
        this.container.find('input[name=daterangepicker_end]').val(dates[1].toString(this.format));
      }
    },

    clickRange: function(e) {
      var label = e.target.innerHTML;
      if (label == this.customLabel) {
        this.container.find('.calendar').show();
      } else {
        var dates = this.ranges[label];

        this.startDate = dates[0];
        this.endDate = dates[1];

        this.leftCalendar.month.set({month: this.startDate.getMonth(), year: this.startDate.getFullYear()});
        this.rightCalendar.month.set({month: this.endDate.getMonth(), year: this.endDate.getFullYear()});
        this.updateCalendars();

        this.notify();
        this.container.find('.calendar').hide();
        this.hide();
      }
    },

    clickPrev: function(e) {
      var cal = $(e.target).parents('.calendar');
      if (cal.hasClass('left')) {
        this.leftCalendar.month.add({ months: -1 });
      } else {
        this.rightCalendar.month.add({ months: -1 });
      }
      this.updateCalendars();
    },

    clickNext: function(e) {
      var cal = $(e.target).parents('.calendar');
      if (cal.hasClass('left')) {
        this.leftCalendar.month.add({ months: 1 });
      } else {
        this.rightCalendar.month.add({ months: 1 });
      }
      this.updateCalendars();
    },

    enterDate: function(e) {

      var title = $(e.target).attr('title');
      var row = title.substr(1,1);
      var col = title.substr(3,1);
      var cal = $(e.target).parents('.calendar');

      if (cal.hasClass('left')) {
        this.container.find('input[name=daterangepicker_start]').val(this.leftCalendar.calendar[row][col].toString(this.format));
      } else {
        this.container.find('input[name=daterangepicker_end]').val(this.rightCalendar.calendar[row][col].toString(this.format));
      }

    },

    clickDate: function(e) {
      var title = $(e.target).attr('title');
      var row = title.substr(1,1);
      var col = title.substr(3,1);
      var cal = $(e.target).parents('.calendar');

      if (cal.hasClass('left')) {
        startDate = this.leftCalendar.calendar[row][col];
        endDate = this.endDate;
      } else {
        startDate = this.startDate;
        endDate = this.rightCalendar.calendar[row][col];
      }

      cal.find('td').removeClass('active');

      if (startDate.equals(endDate) || startDate.isBefore(endDate)) {
        $(e.target).addClass('active');
        this.startDate = startDate;
        this.endDate = endDate;
      }

      this.notify();
    },

    clickApply: function(e) {
      this.notify();
      this.hide();
    },

    updateCalendars: function() {

      this.leftCalendar.calendar = this.buildCalendar(this.leftCalendar.month.getMonth(), this.leftCalendar.month.getFullYear());
      this.rightCalendar.calendar = this.buildCalendar(this.rightCalendar.month.getMonth(), this.rightCalendar.month.getFullYear());
      this.container.find('.calendar.left').html(this.renderCalendar(this.leftCalendar.calendar, this.startDate));
      this.container.find('.calendar.right').html(this.renderCalendar(this.rightCalendar.calendar, this.endDate));

    },

    buildCalendar: function(month, year) {

      var firstDay = Date.today().set({ day: 1, month: month, year: year });
      var lastMonth = firstDay.clone().add(-1).day().getMonth();
      var lastYear = firstDay.clone().add(-1).day().getFullYear();

      var daysInMonth = Date.getDaysInMonth(year, month);
      var daysInLastMonth = Date.getDaysInMonth(lastYear, lastMonth);

      var dayOfWeek = firstDay.getDay();

      //initialize a 6 rows x 7 columns array for the calendar
      var calendar = Array();
      for (var i = 0; i < 6; i++) {
        calendar[i] = Array();
      }

      //populate the calendar with date objects
      var startDay = daysInLastMonth - dayOfWeek + 1;
      if (dayOfWeek == 0)
        startDay = daysInLastMonth - 6;

      var curDate = Date.today().set({ day: startDay, month: lastMonth, year: lastYear });
      for (var i = 0, col = 0, row = 0; i < 42; i++, col++, curDate = curDate.clone().add(1).day()) {
        if (i > 0 && col % 7 == 0) {
          col = 0;
          row++;
        }
        calendar[row][col] = curDate;
      }

      return calendar;

    },

    renderCalendar: function(calendar, selected) {

      var html = '<table class="table-condensed">';
      html += '<thead>';
      html += '<tr>';
      html += '<th class="prev"><i class="icon-arrow-left"></i></th>';
      html += '<th colspan="5">' + calendar[1][1].toString("yyyy - MM") + '</th>';
      html += '<th class="next"><i class="icon-arrow-right"></i></th>';
      html += '</tr>';
      html += '<tr><th>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th>六</th></tr>';
      html += '</thead>';
      html += '<tbody>';

      for (var row = 0; row < 6; row++) {
        html += '<tr>';
        for (var col = 0; col < 7; col++) {
          var cname = (calendar[row][col].getMonth() == calendar[1][1].getMonth()) ? '' : 'off';
          if (calendar[row][col].equals(selected))
            cname = 'active';
          var title = 'r' + row + 'c' + col;
          html += '<td class="' + cname + '" title="' + title + '">' + calendar[row][col].getDate() + '</td>';
        }
        html += '</tr>';
      }

      html += '</tbody>';
      html += '</table>';

      return html;

    }

  };

  DRPTemplate =   '<div class="daterangepicker dropdown-menu">' +
            '<div class="calendar left"></div>' +
            '<div class="calendar right"></div>' +
            '<div class="ranges">' +
              '<div class="range_inputs" style="display: none;">' +
                '<div style="float: left">' +
                  '<label for="daterangepicker_start">从</label>' +
                  '<input class="input-mini" type="text" name="daterangepicker_start" value="" disabled="disabled" />' +
                '</div>' +
                '<div style="float: left; padding-left: 12px">' +
                  '<label for="daterangepicker_end">到</label>' +
                  '<input class="input-mini" type="text" name="daterangepicker_end" value="" disabled="disabled" />' +
                '</div>' +
                '<button class="btn btn-small btn-success" disabled="disabled">确定</button>' +
              '</div>' +
            '</div>' +
          '</div>';

  $.fn.daterangepicker = function(options, cb) { new DateRangePicker(this, options, cb); };

}(window.jQuery);