package main;

import primaryController.PrimaryController;

public class MAGitController
{
    private PrimaryController m_PrimaryController = new PrimaryController();

    public void CreateNewRepositry()
    {
        m_PrimaryController.CreateNewRepository();
    }
}
