#!/usr/bin/env ruby

require 'sling/test'
require 'sling/search'
require 'test/unit.rb'
require 'test/unit/ui/console/testrunner.rb'
include SlingSearch

$testfile1 = "<html><head><title>KERN 312</title></head><body><p>Should work</p></body></html>"

#This kern tests wheter regular users are able to upload files into their private store.

class TC_Kern455Test < SlingTest

  def upload_file(nodename, data)
    res = create_file_node(nodename, "testfile", "testfile", data, "text/html")
    assert_equal(res.code.to_i, 201, "Expexted the file to be created.")
    filepath = "#{nodename}/testfile"
    res = @s.execute_get(@s.url_for(filepath))
    assert_equal(data, res.body, "Expected content to upload cleanly")
    return filepath
  end  

  def test_save_uploaded_file
    m = Time.now.to_i.to_s
    dummyuser = create_user("dummyuser"+m)
    
    # Admin passes fine.
    @s.switch_user(SlingUsers::User.admin_user())
    
	# check what happens when we try and post to a file, check for hashed path creation
	testnode = "_user/private/testnode#{m}"
	res = @s.execute_post(@s.url_for(testnode),{"testprop", "test" })
	assert_equal(res.code.to_i, 201, "Expected the node to be created "+res.body)
		
	assert_equal((res.body.include?"created(\"/_user/private/d0/33/e2/2a/admin/testnode#{m}\""), true, "Expected to find hashed created path "+res.body)
	assert_equal((res.body.include?"modified(\"/_user/private/d0/33/e2/2a/admin/testnode#{m}/testprop\""), true, "Expected to find the hashed created path "+res.body)
	
	res = @s.execute_get(@s.url_for(testnode+".json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the private route "+res.body)
	b1 = res.body

	res = @s.execute_get(@s.url_for("_user/private/d0/33/e2/2a/admin/testnode#{m}.json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the direct route "+res.body)
	assert_equal(b1, res.body, "Expected direct and personal bodies to be the same ")

	# check that the deep create funtionality is working for the admin user.
	testnode = "_user/private/test/n/o/d/e/#{m}"
	res = @s.execute_post(@s.url_for(testnode),{"testprop", "test" })
	assert_equal(res.code.to_i, 201, "Expected the node to be created "+res.body)
		
	assert_equal((res.body.include?"created(\"/_user/private/d0/33/e2/2a/admin/test/n/o/d/e/#{m}\""), true, "Expected to find hashed created path "+res.body)
	assert_equal((res.body.include?"modified(\"/_user/private/d0/33/e2/2a/admin/test/n/o/d/e/#{m}/testprop\""), true, "Expected to find the hashed created path "+res.body)

	res = @s.execute_get(@s.url_for(testnode+".json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the private route "+res.body)
	b1 = res.body

	res = @s.execute_get(@s.url_for("_user/private/d0/33/e2/2a/admin/test/n/o/d/e/#{m}.json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the direct route "+res.body)
	assert_equal(b1, res.body, "Expected direct and personal bodies to be the same ")
	
	
    nodename = "_user/private/upload_test_admin#{m}"
    filepath = upload_file(nodename, $testfile1)
    
    puts("Admin File was uploaded to "+filepath)
    @s.switch_user(dummyuser)

	# check what happens when we try and post to a file, check for hashed path creation
	testnode = "_user/private/testnode#{m}"
	res = @s.execute_post(@s.url_for(testnode),{"testprop", "test" })
	assert_equal(res.code.to_i, 201, "Expected the node to be created "+res.body)
	
	# assume creation worked Ok, now get the node name
	finalPath = res.body.match("/(_user/private/.*?/#{dummyuser.name}/testnode#{m})")[0]
	puts("Path is "+finalPath)	
	
	res = @s.execute_get(@s.url_for(testnode+".json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the private route "+res.body)
	b1 = res.body

	res = @s.execute_get(@s.url_for("#{finalPath}.json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the direct route "+res.body)
	assert_equal(b1, res.body, "Expected direct and personal bodies to be the same ")


	# check that the deep create funtionality is working for the admin user.
	testnode = "_user/private/test/n/o/d/e/#{m}"
	res = @s.execute_post(@s.url_for(testnode),{"testprop", "test" })
	assert_equal(res.code.to_i, 201, "Expected the node to be created "+res.body)
		
	finalPath = res.body.match("/(_user/private/.*?/#{dummyuser.name}/test/n/o/d/e/#{m})")[0]
	
	res = @s.execute_get(@s.url_for(testnode+".json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the private route "+res.body)
	b1 = res.body

	res = @s.execute_get(@s.url_for("#{finalPath}.json"))
	assert_equal(res.code.to_i, 200, "Expected to be able to get to the node via the direct route "+res.body)
	assert_equal(b1, res.body, "Expected direct and personal bodies to be the same ")
	
	
	puts("Normal node operations working Ok on the personal space, if the test works upto this point the problem is with file upload and not permissions per-se ")



    nodename = "_user/private/upload_test_dummyuser#{m}"
    filepath = upload_file(nodename, $testfile1)
    puts("User File was uploaded to "+filepath)
  end



end

Test::Unit::UI::Console::TestRunner.run(TC_Kern455Test)

