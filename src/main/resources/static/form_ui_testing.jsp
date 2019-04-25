          <style>
	        .para_label {
              min-width: 150px !important;
              display: inline-block !important
            }
	      </style>
		  <div class="form-group row">
            <label for="commit_number" class="col-sm-2 col-lg-1 col-form-label para_label">Commit Number</label>
            <div class="col-sm-8 col-lg-5">
              <input type="text" class="form-control" id="commit_number" value="master" placeholder="Commit Number or Branch Name" required>
            </div>
          </div>
		  <div class="form-group row">
            <label class="col-sm-2 col-lg-1 col-form-label para_label">Remote Repository</label>
          </div>
		  <div class="form-group row">
            <label for="unit_test_task" class="col-sm-2 col-lg-1 col-form-label para_label"></label>
            <div class="col-sm-8 col-lg-5">
			  <label class="checkbox-inline">
			    <input type="checkbox" id="unit_test_task" value="test" checked />
			    Check to include unit testing
			  </label>
            </div>
          </div>