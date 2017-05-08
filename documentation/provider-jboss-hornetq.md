# Provider JBoss HornetQ
You can use the commands `cat` and `put` without any modifications. To enable `ls and `count` you need to configure a permission in the `hornetq-configuration.xml` file.

Add a new element to the `<security-settings>` section:

    <security-setting match="jms.queue.hornetq.management">
        <permission type="manage" roles="operator" />
    </security-setting>

You should also asign the permissions _createNonDurableQueue_, _deleteNonDurableQueue_, _consume_ and _send_ to the role, eg:

    <security-setting match="#">
        <permission type="createNonDurableQueue" roles="operator"/>
        <permission type="deleteNonDurableQueue" roles="operator"/>
        <permission type="consume" roles="operator"/>
        <permission type="send" roles="operator"/>
    </security-setting>

The role `operator` has to match a role you defined in the `hornetq-users.xml`file, eg:

    <user name="user" password="user123">
        <role name="operator" />
    </user>

After this you can issue a ls command via:

    pls hornetq://user:user123@localhost:5445

